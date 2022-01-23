package io.penguin.pengiunlettuce;

import com.google.protobuf.ByteString;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.cluster.api.reactive.RedisAdvancedClusterReactiveCommands;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import io.penguin.core.cache.penguin;
import io.penguin.penguincore.exception.TimeoutException;
import io.penguin.penguincore.metric.MetricCreator;
import io.penguin.penguincore.plugin.Ingredient.AllIngredient;
import io.penguin.penguincore.plugin.bulkhead.BulkheadConfiguration;
import io.penguin.penguincore.plugin.bulkhead.BulkheadPlugin;
import io.penguin.penguincore.plugin.circuit.CircuitConfiguration;
import io.penguin.penguincore.plugin.circuit.CircuitPlugin;
import io.penguin.penguincore.plugin.timeout.TimeoutConfiguration;
import io.penguin.penguincore.plugin.timeout.TimeoutPlugin;
import io.penguin.penguincore.reader.BaseCacheReader;
import io.penguin.penguincore.reader.Reader;
import io.penguin.penguincore.util.Pair;
import io.penguin.penguincore.util.ProtoUtil;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Objects;

@Slf4j
public abstract class LettuceCache<K, V> extends BaseCacheReader<K, V> {

    protected final RedisAdvancedClusterReactiveCommands<String, byte[]> reactive;
    private final long expireMilliseconds;
    private final String prefix;
    private final AllIngredient ingredient;

    private final Timer reader = MetricCreator.timer("lettuce_reader", "kind", this.getClass().getSimpleName());
    private final Timer writer = MetricCreator.timer("lettuce_writer", "kind", this.getClass().getSimpleName());
    private final Counter reupdate = MetricCreator.counter("lettuce_reupdate_count", "kind", this.getClass().getSimpleName());

    public LettuceCache(Reader<K, V> fromDownStream, StatefulRedisClusterConnection<String, byte[]> connection, LettuceCacheConfig cacheConfig) throws Exception {
        super(fromDownStream);
        Objects.requireNonNull(cacheConfig);

        this.reactive = connection.reactive();
        this.expireMilliseconds = cacheConfig.getExpireMilliseconds();
        this.prefix = cacheConfig.getPrefix();
        this.ingredient = AllIngredient.builder().build();


        CircuitConfiguration circuitConfiguration = new CircuitConfiguration(cacheConfig.getPluginInput());
        if (circuitConfiguration.support()) {
            ingredient.setCircuitIngredient(circuitConfiguration.generate(this.getClass()));
        }

        TimeoutConfiguration timeoutConfiguration = new TimeoutConfiguration(cacheConfig.getPluginInput());
        if (timeoutConfiguration.support()) {
            ingredient.setTimeoutIngredient(timeoutConfiguration.generate(this.getClass()));
        }

        BulkheadConfiguration bulkheadConfiguration = new BulkheadConfiguration(cacheConfig.getPluginInput());
        if (bulkheadConfiguration.support()) {
            ingredient.setBulkheadIngredient(bulkheadConfiguration.generate(this.getClass()));
        }

    }

    @Override
    public void writeOne(String key, V value) {
        long start = System.currentTimeMillis();
        reactive.setex(this.prefix() + key, this.expireMilliseconds, withTime(value))
                .doOnSuccess(i -> writer.record(Duration.ofMillis(System.currentTimeMillis() - start)))
                .subscribe();
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
    public Mono<V> findOne(K key) {

        long start = System.currentTimeMillis();
        Mono<V> mono = reactive.get(key.toString())
                .map(i -> ProtoUtil.safeParseFrom(penguin.Codec.parser(), i, penguin.Codec.newBuilder().getDefaultInstanceForType()))
                .map(i -> Pair.of(i.getTimestamp(), deserialize(i.getPayload().toByteArray())))
                .doOnNext(i -> {
                    if (i.getKey() + expireMilliseconds > System.currentTimeMillis()) {
                        reupdate.increment();
                        writeOne(key.toString(), i.getValue());
                    }
                })
                .map(Pair::getValue);

        mono = new BulkheadPlugin<>(mono, ingredient);
        mono = new TimeoutPlugin<>(mono, ingredient);
        mono = new CircuitPlugin<>(mono, ingredient);


        return mono
                .doOnError(e -> {
                    if (!(e instanceof TimeoutException) && !(e instanceof CallNotPermittedException)) {
                        log.error("", e);
                    }
                })
                .onErrorReturn(this.failFindOne(key))
                .doOnSuccess(i -> reader.record(Duration.ofMillis(System.currentTimeMillis() - start)));
    }


    abstract public byte[] serialize(V v);

    abstract public V deserialize(byte[] bytes);

    private byte[] withTime(V v) {
        return penguin.Codec.newBuilder()
                .setTimestamp(System.currentTimeMillis())
                .setPayload(ByteString.copyFrom(serialize(v)))
                .build().toByteArray();
    }


}
