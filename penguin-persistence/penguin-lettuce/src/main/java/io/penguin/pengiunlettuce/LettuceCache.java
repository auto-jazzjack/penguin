package io.penguin.pengiunlettuce;

import com.google.protobuf.ByteString;
import io.lettuce.core.cluster.api.reactive.RedisAdvancedClusterReactiveCommands;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import io.penguin.core.cache.penguin;
import io.penguin.pengiunlettuce.cofig.LettuceCacheIngredient;
import io.penguin.pengiunlettuce.connection.LettuceConnectionIngredient;
import io.penguin.pengiunlettuce.connection.RedisConnection;
import io.penguin.penguincodec.Codec;
import io.penguin.penguincore.metric.MetricCreator;
import io.penguin.penguincore.plugin.Ingredient.AllIngredient;
import io.penguin.penguincore.plugin.Plugin;
import io.penguin.penguincore.plugin.bulkhead.BulkheadConfiguration;
import io.penguin.penguincore.plugin.bulkhead.BulkheadPlugin;
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

    protected final RedisAdvancedClusterReactiveCommands<String, byte[]> reactive;
    private final long expireMilliseconds;
    private final String prefix;

    private final Timer reader = MetricCreator.timer("lettuce_reader", "kind", this.getClass().getSimpleName());
    private final Timer writer = MetricCreator.timer("lettuce_writer", "kind", this.getClass().getSimpleName());
    private final Counter reupdate = MetricCreator.counter("lettuce_reupdate_count", "kind", this.getClass().getSimpleName());
    private final Plugin<CacheContext<V>>[] plugins;
    private final Codec<V> codec;
    private final Sinks.Many<Pair<K, CacheContext<V>>> watcher;


    public LettuceCache(LettuceConnectionIngredient connection, LettuceCacheIngredient<K, V> cacheConfig) throws Exception {

        Objects.requireNonNull(cacheConfig);
        this.codec = cacheConfig.getCodec();

        this.reactive = RedisConnection.connection(connection).reactive();
        this.expireMilliseconds = connection.getExpireMilliseconds();
        this.prefix = cacheConfig.getPrefix();
        AllIngredient ingredient = AllIngredient.builder().build();

        List<Plugin<Object>> pluginList = new ArrayList<>();
        TimeoutConfiguration timeoutConfiguration = new TimeoutConfiguration(cacheConfig.getPluginInput());
        if (timeoutConfiguration.support()) {
            ingredient.setTimeoutIngredient(timeoutConfiguration.generate(this.getClass()));
            pluginList.add(new TimeoutPlugin<>(ingredient.getTimeoutIngredient()));
        }

        BulkheadConfiguration bulkheadConfiguration = new BulkheadConfiguration(cacheConfig.getPluginInput());
        if (bulkheadConfiguration.support()) {
            ingredient.setBulkheadIngredient(bulkheadConfiguration.generate(this.getClass()));
            pluginList.add(new BulkheadPlugin<>(ingredient.getBulkheadIngredient()));
        }

        CircuitConfiguration circuitConfiguration = new CircuitConfiguration(cacheConfig.getPluginInput());
        if (circuitConfiguration.support()) {
            ingredient.setCircuitIngredient(circuitConfiguration.generate(this.getClass()));
            pluginList.add(new CircuitPlugin<>(ingredient.getCircuitIngredient()));
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
        reactive.setex(this.prefix() + key, this.expireMilliseconds, serialize(value))
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
                .map(this::deserialize)
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


    public byte[] serialize(CacheContext<V> v) {
        if (v == null || v.getValue() == null) {
            return new byte[0];
        }

        try {
            return penguin.CacheCodec.newBuilder()
                    .setPayload(ByteString.copyFrom(this.codec.serialize(v.getValue())))
                    .setTimestamp(v.getTimeStamp())
                    .build().toByteArray();
        } catch (Exception e) {
            log.error("", e);
            throw new IllegalStateException("Cannot serialize " + v);
        }
    }

    public CacheContext<V> deserialize(byte[] bytes) {

        try {
            penguin.CacheCodec cacheCodec = penguin.CacheCodec.parseFrom(bytes);
            V deserialize = codec.deserialize(cacheCodec.getPayload().toByteArray());
            return new CacheContext<>() {
                @Override
                public V getValue() {
                    return deserialize;
                }

                @Override
                public long getTimeStamp() {
                    return cacheCodec.getTimestamp();
                }
            };
        } catch (Exception e) {
            log.error("", e);
            throw new IllegalStateException("Cannot deserialize " + new String(bytes));
        }
    }
}
