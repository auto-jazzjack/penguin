package io.penguin.pengiuncassandra;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.MappingManager;
import com.datastax.driver.mapping.Result;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import io.penguin.pengiuncassandra.config.CassandraSourceConfig;
import io.penguin.pengiuncassandra.connection.CassandraResource;
import io.penguin.pengiuncassandra.util.CasandraUtil;
import io.penguin.penguincore.metric.MetricCreator;
import io.penguin.penguincore.plugin.Plugin;
import io.penguin.penguincore.plugin.bulkhead.BulkHeadPlugin;
import io.penguin.penguincore.plugin.bulkhead.BulkheadGenerator;
import io.penguin.penguincore.plugin.circuit.CircuitGenerator;
import io.penguin.penguincore.plugin.circuit.CircuitPlugn;
import io.penguin.penguincore.plugin.timeout.TimeoutGenerator;
import io.penguin.penguincore.plugin.timeout.TimeoutPlugin;
import io.penguin.penguincore.reader.Reader;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.*;
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
    private final List<Plugin<V>> plugins;
    private final Counter failed = MetricCreator.counter("cassandra_reader",
            "kind", this.getClass().getSimpleName(), "type", "success");

    private final Counter success = MetricCreator.counter("cassandra_reader",
            "kind", this.getClass().getSimpleName(), "type", "failed");

    private final Timer latency = MetricCreator.timer("cassandra_latency",
            "kind", this.getClass().getSimpleName());

    static Map<CassandraResource, Session> cached = new HashMap<>();

    public CassandraSource(CassandraResource cassandraResource, CassandraSourceConfig<V> cassandraSourceConfig) {
        valueType = cassandraSourceConfig.getValueType();
        this.session = connect(cassandraResource);

        plugins = new ArrayList<>();

        TimeoutGenerator timeoutConfiguration = new TimeoutGenerator(cassandraSourceConfig.getTimeout());
        if (timeoutConfiguration.support()) {
            plugins.add(new TimeoutPlugin<>(timeoutConfiguration.generate(this.getClass())));
        }
        BulkheadGenerator<V> bulkheadGenerator = new BulkheadGenerator<>(cassandraSourceConfig.getBulkhead());
        if (bulkheadGenerator.support()) {
            plugins.add(new BulkHeadPlugin<>(bulkheadGenerator.generate(this.getClass())));
        }
        CircuitGenerator<V> circuitGenerator = new CircuitGenerator<>(cassandraSourceConfig.getCircuit());
        if (circuitGenerator.support()) {
            plugins.add(new CircuitPlugn<>(circuitGenerator.generate(this.getClass())));
        }


        mappingManager = new MappingManager(session);
        mappingManager.mapper(valueType, cassandraResource.getKeySpace());

        this.statement = this.mappingManager.getSession().prepare(CasandraUtil.queryGenerator(cassandraSourceConfig.getValueType()));
    }


    public synchronized static Session connect(CassandraResource config) {

        if (cached.get(config) != null) {
            return cached.get(config);
        }
        Cluster.Builder builder = Cluster.builder();

        Optional.of(config).map(CassandraResource::getHosts)
                .map(i -> i.split(","))
                .map(Arrays::asList)
                .ifPresent(i -> builder.addContactPoints(i.toArray(String[]::new)));

        cached.put(config, builder.build().connect(config.getKeySpace()));
        return cached.get(config);
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
                .subscribeOn(Schedulers.boundedElastic());
    }
}
