package io.penguin.pengiunlettuce;

import io.lettuce.core.cluster.api.reactive.RedisAdvancedClusterReactiveCommands;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import io.penguin.pengiunlettuce.cofig.LettuceCacheConfig;
import io.penguin.pengiunlettuce.connection.LettuceResource;
import io.penguin.pengiunlettuce.connection.RedisConnectionFactory;
import io.penguin.penguincore.metric.MetricCreator;
import io.penguin.penguincore.plugin.Ingredient.Decorators;
import io.penguin.penguincore.plugin.Plugin;
import io.penguin.penguincore.plugin.circuit.CircuitConfiguration;
import io.penguin.penguincore.plugin.circuit.CircuitPlugin;
import io.penguin.penguincore.plugin.timeout.TimeoutConfiguration;
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
import java.util.Objects;
import java.util.function.Function;

@Slf4j
public class LettuceCache<K, V> implements BaseCacheReader<K, V> {

    protected final RedisAdvancedClusterReactiveCommands<String, CacheContext<V>> reactive;
    private final long expireMilliseconds;
    private final String prefix;

    private final Timer reader = MetricCreator.timer("lettuce_reader", "kind", this.getClass().getSimpleName());
    private final Timer writer = MetricCreator.timer("lettuce_writer", "kind", this.getClass().getSimpleName());
    private final Counter reupdate = MetricCreator.counter("lettuce_reupdate_count", "kind", this.getClass().getSimpleName());
    private final Plugin<CacheContext<V>>[] plugins;
    private final Sinks.Many<Pair<K, CacheContext<V>>> watcher;


    public LettuceCache(LettuceResource lettuceResource, LettuceCacheConfig<V> cacheConfig) throws Exception {

        Objects.requireNonNull(cacheConfig);

        this.reactive = RedisConnectionFactory.connection(lettuceResource, cacheConfig).reactive();
        this.expireMilliseconds = cacheConfig.getExpireMilliseconds();
        this.prefix = cacheConfig.getPrefix();
        Decorators ingredient = Decorators.builder().build();

        List<Plugin<Object>> pluginList = new ArrayList<>();
        TimeoutConfiguration timeoutConfiguration = new TimeoutConfiguration(cacheConfig.getTimeoutModel());
        if (timeoutConfiguration.support()) {
            ingredient.setTimeoutDecorator(timeoutConfiguration.generate(this.getClass()));
            pluginList.add(new TimeoutPlugin<>(ingredient.getTimeoutDecorator()));
        }


        CircuitConfiguration circuitConfiguration = new CircuitConfiguration(cacheConfig.getCircuitModel());
        if (circuitConfiguration.support()) {
            ingredient.setCircuitDecorator(circuitConfiguration.generate(this.getClass()));
            pluginList.add(new CircuitPlugin<>(ingredient.getCircuitDecorator()));
        }


        plugins = pluginList.toArray(new Plugin[0]);

        watcher = Sinks.many()
                .unicast()
                .onBackpressureBuffer();

        watcher.asFlux()
                .windowTimeout(100, Duration.ofSeconds(5))
                .flatMap(i -> i.distinct((Function<Pair<K, CacheContext<V>>, Object>) kCacheContextPair -> kCacheContextPair.getKey()))
                .subscribe(i -> writeOne0(i.getKey(), i.getValue()), e -> log.error("", e));
    }

    private void writeOne0(K key, CacheContext<V> value) {

        if (value == null || value.getTimeStamp() <= 0) {
            return;
        }

        long start = System.currentTimeMillis();
        reactive.setex(this.prefix() + key, this.expireMilliseconds, value)
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
        Mono<CacheContext<V>> mono = reactive.get(this.prefix + key.toString())
                .publishOn(Schedulers.parallel())
                //.map(this::deserialize)
                .doOnNext(i -> {
                    if (i.getTimeStamp() + expireMilliseconds < System.currentTimeMillis()) {
                        reupdate.increment();
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
