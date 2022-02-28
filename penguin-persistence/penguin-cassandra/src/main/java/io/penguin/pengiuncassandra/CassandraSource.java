package io.penguin.pengiuncassandra;

import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.MappingManager;
import com.datastax.driver.mapping.Result;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import io.penguin.pengiuncassandra.config.CassandraIngredient;
import io.penguin.pengiuncassandra.util.CasandraUtil;
import io.penguin.penguincore.metric.MetricCreator;
import io.penguin.penguincore.plugin.Ingredient.AllIngredient;
import io.penguin.penguincore.plugin.Plugin;
import io.penguin.penguincore.plugin.timeout.TimeoutConfiguration;
import io.penguin.penguincore.plugin.timeout.TimeoutPlugin;
import io.penguin.penguincore.reader.Context;
import io.penguin.penguincore.reader.Reader;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/***
 * Cassandra Source only use timeOutOperator
 *
 * Circuiting to Source repository is meaningless.
 *
 * */
public class CassandraSource<K, V> implements Reader<K, Context<V>> {

    private final Class<V> valueType;
    private final MappingManager mappingManager;
    private final PreparedStatement statement;
    private final Session session;
    private final Plugin<Context<V>>[] plugins;
    private final Counter failed = MetricCreator.counter("cassandra_reader",
            "kind", this.getClass().getSimpleName(), "type", "success");

    private final Counter success = MetricCreator.counter("cassandra_reader",
            "kind", this.getClass().getSimpleName(), "type", "failed");

    private final Timer latency = MetricCreator.timer("cassandra_latency",
            "kind", this.getClass().getSimpleName());

    public CassandraSource(CassandraIngredient cassandraConfig) {
        valueType = (Class<V>) cassandraConfig.getValueType();
        this.session = cassandraConfig.getSession();
        AllIngredient ingredient = AllIngredient.builder().build();

        List<Plugin<Object>> pluginList = new ArrayList<>();

        TimeoutConfiguration timeoutConfiguration = new TimeoutConfiguration(cassandraConfig.getPluginInput());
        if (timeoutConfiguration.support()) {
            ingredient.setTimeoutIngredient(timeoutConfiguration.generate(this.getClass()));
            pluginList.add(new TimeoutPlugin<>(ingredient.getTimeoutIngredient()));
        }

        this.mappingManager = CasandraUtil.mappingManager(
                this.session,
                this.valueType,
                cassandraConfig.getKeyspace()
        );

        this.statement = this.mappingManager.getSession().prepare(CasandraUtil.queryGenerator(cassandraConfig.getKeyspace(),
                cassandraConfig.getTable(),
                cassandraConfig.getColumns(),
                cassandraConfig.getIdColumn()));
        plugins = pluginList.toArray(new Plugin[0]);
    }


    @Override
    public Mono<Context<V>> findOne(K key) {

        long start = System.currentTimeMillis();
        ListenableFuture<Result<V>> resultListenableFuture = mappingManager.mapper(valueType)
                .mapAsync(session.executeAsync(statement.bind(key)));

        Mono<Context<V>> mono = Mono.create(i -> Futures.addCallback(resultListenableFuture, new FutureCallback<>() {
            @Override
            public void onSuccess(Result<V> result) {

                V v = Optional.ofNullable(result)
                        .map(Result::one)
                        .orElse(null);

                i.success(Context.<V>builder()
                        .value(v).build());
            }

            @Override
            public void onFailure(Throwable t) {
                i.error(t);
            }
        }));

        for (Plugin<Context<V>> plugin : plugins) {
            mono = plugin.decorateSource(mono);
        }

        return mono
                .doOnError(i -> failed.increment())
                .doOnSuccess(i -> {
                    success.increment();
                    latency.record(System.currentTimeMillis() - start, TimeUnit.MILLISECONDS);
                });
    }
}
