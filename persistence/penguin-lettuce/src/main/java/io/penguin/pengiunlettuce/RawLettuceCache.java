package io.penguin.pengiunlettuce;

import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.cluster.api.reactive.RedisAdvancedClusterReactiveCommands;
import io.penguin.penguincore.reader.BaseCacheReader;
import io.penguin.penguincore.reader.Reader;
import reactor.core.publisher.Mono;

import java.util.Optional;

abstract class RawLettuceCache<K, V> extends BaseCacheReader<K, V> {

    protected final RedisAdvancedClusterReactiveCommands<K, byte[]> reactive;
    private final long expireSecond;

    public RawLettuceCache(Reader<K, V> fromDownStream, StatefulRedisClusterConnection<K, byte[]> connection, LettuceCacheConfig pureLettuceCacheConfig) {
        super(fromDownStream);
        this.reactive = connection.reactive();
        this.expireSecond = Optional.of(pureLettuceCacheConfig).map(LettuceCacheConfig::getExpireTime)
                .orElse(0L);
    }


    @Override
    public void writeOne(K key, V value) {
        reactive.setex(key, this.expireSecond, serialize(value))
                .subscribe();
    }

    @Override
    public long expireSecond() {
        return this.expireSecond;
    }

    @Override
    public Mono<V> findOne(K key) {
        return reactive.get(key)
                .map(this::deserialize);
    }

    abstract public byte[] serialize(V v);

    abstract public V deserialize(byte[] bytes);


}
