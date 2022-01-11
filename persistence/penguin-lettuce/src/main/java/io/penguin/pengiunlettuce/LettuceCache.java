package io.penguin.pengiunlettuce;

import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.cluster.api.reactive.RedisAdvancedClusterReactiveCommands;
import io.penguin.penguincore.reader.BaseCacheReader;
import io.penguin.penguincore.reader.Context;
import io.penguin.penguincore.reader.Reader;
import reactor.core.publisher.Mono;

import java.util.Optional;

public abstract class LettuceCache<K, V> extends BaseCacheReader<K, V> {

    private final RedisAdvancedClusterReactiveCommands<K, byte[]> reactive;
    private final long expireTime;

    public LettuceCache(Reader<K, V> fromDownStream, StatefulRedisClusterConnection<K, byte[]> connection, LettuceCacheConfig lettuceCacheConfig) {
        super(fromDownStream);
        this.reactive = connection.reactive();

        this.expireTime = Optional.of(lettuceCacheConfig).map(LettuceCacheConfig::getExpireTime).orElse(0L);
    }

    @Override
    public Mono<Boolean> writeOne(K key, V value) {
        return reactive.setex(key, this.expireTime, serialize(value))
                .map(i -> true);
    }

    @Override
    public long expireTime() {
        return this.expireTime;
    }

    @Override
    public Mono<Context<V>> findOne(K key) {
        return reactive.get(key)
                .map(this::deserialize)
                .map(i-> Context.<V>builder()
                        .value(i)
                        .from(Context.From.CACHE1)
                        .build());
    }

    abstract public byte[] serialize(V v);

    abstract public V deserialize(byte[] bytes);
}
