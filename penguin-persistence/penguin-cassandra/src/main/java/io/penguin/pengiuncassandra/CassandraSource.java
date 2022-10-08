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
import io.penguin.pengiuncassandra.connection.CassandraConnectionIngredient;
import io.penguin.pengiuncassandra.config.CassandraIngredient;
import io.penguin.pengiuncassandra.util.CasandraUtil;
import io.penguin.penguincore.metric.MetricCreator;
import io.penguin.penguincore.plugin.Ingredient.AllIngredient;
import io.penguin.penguincore.plugin.Plugin;
import io.penguin.penguincore.plugin.timeout.TimeoutConfiguration;
import io.penguin.penguincore.plugin.timeout.TimeoutPlugin;
import io.penguin.penguincore.reader.CacheContext;
import io.penguin.penguincore.reader.Reader;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

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
public class CassandraSource<K, V> implements Reader<K, V> {

    private final Class<V> valueType;
    private final MappingManager mappingManager;
    private final PreparedStatement statement;
    private final Session session;
    private final Plugin<V>[] plugins;
    private final Counter failed = MetricCreator.counter("cassandra_reader",
            "kind", this.getClass().getSimpleName(), "type", "success");

    private final Counter success = MetricCreator.counter("cassandra_reader",
            "kind", this.getClass().getSimpleName(), "type", "failed");

    private final Timer latency = MetricCreator.timer("cassandra_latency",
            "kind", this.getClass().getSimpleName());

    public CassandraSource(CassandraConnectionIngredient connection, CassandraIngredient cassandraConfig) {
        valueType = (Class<V>) cassandraConfig.getValueType();
        this.session = connection.getSession();
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
                connection.getKeyspace()
        );

        this.statement = this.mappingManager.getSession().prepare(CasandraUtil.queryGenerator(connection.getKeyspace(),
                cassandraConfig.getTable(),
                cassandraConfig.getColumns(),
                cassandraConfig.getIdColumn()));
        plugins = pluginList.toArray(new Plugin[0]);
    }


    @Override
    public Mono<V> findOne(K key) {

        long start = System.currentTimeMillis();
        ListenableFuture<Result<V>> resultListenableFuture = mappingManager.mapper(valueType)
                .mapAsync(session.executeAsync(statement.bind(key)));

        Mono<V> mono = Mono.create(i -> Futures.addCallback(resultListenableFuture, new FutureCallback<>() {
            @Override
            public void onSuccess(Result<V> result) {

                V v = Optional.ofNullable(result)
                        .map(Result::one)
                        .orElse(null);

                if (v != null) {
                    i.success(v);
                } else {
                    i.success();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                i.error(t);
            }
        }));

        for (Plugin<V> plugin : plugins) {
            mono = plugin.decorateSource(mono);
        }

        return mono
                .doOnError(i -> failed.increment())
                .doOnSuccess(i -> {
                    success.increment();
                    latency.record(System.currentTimeMillis() - start, TimeUnit.MILLISECONDS);
                })
                .subscribeOn(Schedulers.parallel());
    }
}
