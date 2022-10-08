package io.penguin.pengiunlettuce;

import io.lettuce.core.cluster.api.reactive.RedisAdvancedClusterReactiveCommands;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import io.penguin.core.cache.penguin;
import io.penguin.pengiunlettuce.cofig.LettuceCacheIngredient;
import io.penguin.pengiunlettuce.connection.LettuceConnectionIngredient;
import io.penguin.pengiunlettuce.connection.RedisConnection;
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
import io.penguin.penguincore.reader.ProtoCacheContext;
import io.penguin.penguincore.util.ProtoUtil;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
public class LettuceCache<K, V> extends BaseCacheReader<K, V> {

    protected final RedisAdvancedClusterReactiveCommands<String, byte[]> reactive;
    private final long expireMilliseconds;
    private final String prefix;

    private final Timer reader = MetricCreator.timer("lettuce_reader", "kind", this.getClass().getSimpleName());
    private final Timer writer = MetricCreator.timer("lettuce_writer", "kind", this.getClass().getSimpleName());
    private final Counter reupdate = MetricCreator.counter("lettuce_reupdate_count", "kind", this.getClass().getSimpleName());
    private final Plugin<CacheContext>[] plugins;


    public LettuceCache(LettuceConnectionIngredient connection, LettuceCacheIngredient<K, V> cacheConfig) throws Exception {

        super(cacheConfig.getFromDownStream());
        Objects.requireNonNull(cacheConfig);

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

    }

    @Override
    public void writeOne(String key, CacheContext value) {

        if (value == null) {
            return;
        }

        long start = System.currentTimeMillis();
        reactive.setex(this.prefix() + key, this.expireMilliseconds, value.getValue())
                .doOnSuccess(i -> writer.record(Duration.ofMillis(System.currentTimeMillis() - start)))
                .subscribeOn(Schedulers.parallel()).subscribe();
    }

    @Override
    public String prefix() {
        return this.prefix;
    }

    @Override
    public long expireSecond() {
        return this.expireMilliseconds;
    }

    @Override
    public Mono<CacheContext> findOne(K key) {

        long start = System.currentTimeMillis();
        Mono<CacheContext> mono = reactive.get(this.prefix + key.toString())
                .publishOn(Schedulers.parallel())
                .map(i -> ProtoUtil.safeParseFrom(penguin.CacheCodec.parser(), i, penguin.CacheCodec.newBuilder().getDefaultInstanceForType()))
                .map(ProtoCacheContext::new)
                .doOnNext(i -> {
                    if (i.getTimeStamp() + expireMilliseconds < System.currentTimeMillis()) {
                        reupdate.increment();
                        writeOne(key.toString(), i);
                    }
                })
                .map(i -> i);

        for (Plugin<CacheContext> plugin : plugins) {
            mono = plugin.decorateSource(mono);
        }

        return mono.onErrorReturn(this.failFindOne(key))
                .doOnError(e -> log.error("", e))
                .doOnSuccess(i -> reader.record(Duration.ofMillis(System.currentTimeMillis() - start)));
    }

    @Override
    public CacheContext failFindOne(K key) {
        return null;
    }


}
