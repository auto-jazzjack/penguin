package io.penguin.pengiunlettuce;

import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.cluster.api.reactive.RedisAdvancedClusterReactiveCommands;
import io.penguin.penguincore.plugin.Ingredient.AllIngredient;
import io.penguin.penguincore.plugin.circuit.CircuitPluggable;
import io.penguin.penguincore.plugin.circuit.CircuitPlugin;
import io.penguin.penguincore.plugin.timeout.TimeoutPluggable;
import io.penguin.penguincore.plugin.timeout.TimeoutPlugin;
import io.penguin.penguincore.reader.BaseCacheReader;
import io.penguin.penguincore.reader.Reader;
import reactor.core.publisher.Mono;

import java.util.Objects;

public abstract class LettuceCache<K, V> extends BaseCacheReader<K, V> {

    protected final RedisAdvancedClusterReactiveCommands<String, byte[]> reactive;
    private final long expireSecond;
    private final String prefix;
    private final AllIngredient ingredient;

    public LettuceCache(Reader<K, V> fromDownStream, StatefulRedisClusterConnection<String, byte[]> connection, LettuceCacheConfig cacheConfig) throws Exception {
        super(fromDownStream);
        Objects.requireNonNull(cacheConfig);

        this.reactive = connection.reactive();
        this.expireSecond = cacheConfig.getExpireTime();
        this.prefix = cacheConfig.getPrefix();
        this.ingredient = AllIngredient.builder().build();


        CircuitPluggable circuitPluggable = new CircuitPluggable(cacheConfig.getPluginInput());
        if (circuitPluggable.support()) {
            ingredient.setCircuitIngredient(circuitPluggable.generate());
        }

        TimeoutPluggable timeoutPluggable = new TimeoutPluggable(cacheConfig.getPluginInput());
        if (timeoutPluggable.support()) {
            ingredient.setTimeoutIngredient(timeoutPluggable.generate());
        }

    }


    @Override
    public void writeOne(String key, V value) {
        reactive.setex(this.prefix() + key, this.expireSecond, serialize(value))
                .subscribe();
    }

    @Override
    public String prefix() {
        return this.prefix;
    }

    @Override
    public long expireSecond() {
        return this.expireSecond;
    }

    @Override
    public Mono<V> findOne(K key) {

        Mono<V> mono = reactive.get(key.toString()).map(this::deserialize);
        mono = new TimeoutPlugin<>(mono, ingredient);
        mono = new CircuitPlugin<>(mono, ingredient);

        return mono.onErrorReturn(failFindOne(key));
    }


    abstract public byte[] serialize(V v);

    abstract public V deserialize(byte[] bytes);


}
