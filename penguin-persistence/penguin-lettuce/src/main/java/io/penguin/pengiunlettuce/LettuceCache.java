package io.penguin.pengiunlettuce;

import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import io.penguin.pengiunlettuce.cofig.LettuceCacheConfig;
import io.penguin.pengiunlettuce.connection.LettuceResource;
import io.penguin.pengiunlettuce.connection.RedisConnectionFactory;
import io.penguin.penguincore.metric.MetricCreator;
import io.penguin.penguincore.plugin.Plugin;
import io.penguin.penguincore.plugin.circuit.CircuitGenerator;
import io.penguin.penguincore.plugin.circuit.CircuitPlugn;
import io.penguin.penguincore.plugin.timeout.TimeoutGenerator;
import io.penguin.penguincore.plugin.timeout.TimeoutPlugin;
import io.penguin.penguincore.reader.BaseCacheReader;
import io.penguin.penguincore.reader.CacheContext;
import io.penguin.penguincore.util.Pair;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Slf4j
public class LettuceCache<K, V> implements BaseCacheReader<K, V> {

    protected final StatefulRedisClusterConnection<String, CacheContext<V>> statefulConnection;
    private final long expireMilliseconds;
    private final String prefix;

    private final Timer reader = MetricCreator.timer("lettuce_reader", "kind", this.getClass().getSimpleName());
    private final Timer writer = MetricCreator.timer("lettuce_writer", "kind", this.getClass().getSimpleName());
    private final Counter reUpdate = MetricCreator.counter("lettuce_reUpdate_count", "kind", this.getClass().getSimpleName());
    private final List<Plugin<CacheContext<V>>> plugins;
    private final Sinks.Many<Pair<K, CacheContext<V>>> watcher;

    private static final Map<LettuceResource, StatefulRedisClusterConnection<String, CacheContext<Object>>> cached = new ConcurrentHashMap<>();


    public LettuceCache(LettuceResource lettuceResource, LettuceCacheConfig<V> cacheConfig) throws Exception {

        Objects.requireNonNull(cacheConfig);

        this.statefulConnection = connection(lettuceResource, cacheConfig);
        this.expireMilliseconds = cacheConfig.getExpireMilliseconds();
        this.prefix = cacheConfig.getPrefix();

        plugins = new ArrayList<>();
        TimeoutGenerator timeoutConfiguration = new TimeoutGenerator(cacheConfig.getTimeout());
        if (timeoutConfiguration.support()) {
            plugins.add(new TimeoutPlugin<>(timeoutConfiguration.generate(this.getClass())));
        }

        CircuitGenerator<CacheContext<V>> circuitConfiguration = new CircuitGenerator<>(cacheConfig.getCircuit());
        if (circuitConfiguration.support()) {
            plugins.add(new CircuitPlugn<>(circuitConfiguration.generate(this.getClass())));
        }

        watcher = Sinks.many()
                .unicast()
                .onBackpressureBuffer();

        watcher.asFlux()
                .windowTimeout(100, Duration.ofSeconds(5))
                .flatMap(i -> i.distinct((Function<Pair<K, CacheContext<V>>, Object>) kCacheContextPair -> kCacheContextPair.getKey()))
                .subscribe(i -> writeOne0(i.getKey(), i.getValue()), e -> log.error("", e));
    }

    private synchronized StatefulRedisClusterConnection<String, CacheContext<V>> connection(LettuceResource resource, LettuceCacheConfig<V> cacheConfig) {
        if (cached.get(resource) == null) {
            StatefulRedisClusterConnection<String, CacheContext<V>> connection = RedisConnectionFactory.connection(resource, cacheConfig);
            cached.put(resource, (StatefulRedisClusterConnection) connection);
        }
        return (StatefulRedisClusterConnection) cached.get(resource);
    }

    private void writeOne0(K key, CacheContext<V> value) {

        if (value == null || value.getTimeStamp() <= 0) {
            return;
        }

        long start = System.currentTimeMillis();
        statefulConnection.reactive().setex(this.prefix() + key, this.expireMilliseconds, value)
                .doOnSuccess(i -> writer.record(Duration.ofMillis(System.currentTimeMillis() - start)))
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe();
    }

    @Override
    public void writeOne(K key, CacheContext<V> value) {
        if (key != null && value != null && value.getValue() != null) {
            this.watcher.tryEmitNext(Pair.of(key, value));
        }
    }

    @Override
    public String prefix() {
        return this.prefix;
    }

    @Override
    public String cacheName() {
        return BaseCacheReader.super.cacheName();
    }

    @Override
    public Mono<CacheContext<V>> findOne(K key) {

        long start = System.currentTimeMillis();
        Mono<CacheContext<V>> mono = statefulConnection.reactive().get(this.prefix + key.toString())
                .publishOn(Schedulers.parallel())
                .doOnNext(i -> {
                    if (i.getTimeStamp() + expireMilliseconds < System.currentTimeMillis()) {
                        reUpdate.increment();
                        writeOne(key, i);
                    }
                });

        for (Plugin<CacheContext<V>> plugin : plugins) {
            mono = plugin.decorateSource(mono);
        }

        return mono
                .onErrorResume(throwable -> failFindOne(key, throwable))
                .doOnError(e -> log.error("", e))
                .doOnSuccess(i -> reader.record(Duration.ofMillis(System.currentTimeMillis() - start)));
    }


}
