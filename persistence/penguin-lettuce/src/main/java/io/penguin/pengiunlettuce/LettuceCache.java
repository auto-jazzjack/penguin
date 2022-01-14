package io.penguin.pengiunlettuce;

import io.lettuce.core.RedisURI;
import io.lettuce.core.TimeoutOptions;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.cluster.api.reactive.RedisAdvancedClusterReactiveCommands;
import io.lettuce.core.codec.CompressionCodec;
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

        RedisClusterClient redisClusterClient = RedisClusterClient.create(RedisURI.builder()
                .build());

        redisClusterClient.setOptions(ClusterClientOptions.builder()
                .topologyRefreshOptions(ClusterTopologyRefreshOptions.builder()
                        .enableAllAdaptiveRefreshTriggers()
                        .enablePeriodicRefresh()
                        .build())
                .requestQueueSize(lettuceCacheConfig.getQueueSize())
                .timeoutOptions(TimeoutOptions.builder()
                        .build())
                .build());


        StatefulRedisClusterConnection<?, byte[]> connect = redisClusterClient.connect(CompressionCodec.valueCompressor(lettuceCacheConfig.getCodec(), CompressionCodec.CompressionType.GZIP));
        this.reactive = (RedisAdvancedClusterReactiveCommands<K, byte[]>) connect.reactive();
        this.expireTime = Optional.of(lettuceCacheConfig).map(LettuceCacheConfig::getExpireTime).orElse(0L);
    }

    @Override
    public void writeOne(K key, V value) {
        reactive.setex(key, this.expireTime, serialize(value));
    }

    @Override
    public long expireTime() {
        return this.expireTime;
    }

    @Override
    public Mono<V> findOne(K key) {
        return reactive.get(key)
                .map(this::deserialize);
    }

    abstract public byte[] serialize(V v);

    abstract public V deserialize(byte[] bytes);


}
