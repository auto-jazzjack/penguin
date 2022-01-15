package io.penguin.pengiunlettuce;

import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.cluster.api.reactive.RedisAdvancedClusterReactiveCommands;
import io.penguin.penguincore.plugin.PluginComposer;
import io.penguin.penguincore.reader.BaseCacheReader;
import io.penguin.penguincore.reader.Reader;
import reactor.core.publisher.Mono;

import java.util.Objects;

public abstract class LettuceCache<K, V> extends BaseCacheReader<K, V> {

    protected final RedisAdvancedClusterReactiveCommands<String, byte[]> reactive;
    private final Reader<String, V> pluginAdoptedCaller;
    private final long expireSecond;
    private String prefix;

    public LettuceCache(Reader<K, V> fromDownStream, StatefulRedisClusterConnection<String, byte[]> connection, LettuceCacheConfig cacheConfig) throws Exception {
        super(fromDownStream);
        Objects.requireNonNull(cacheConfig);

        this.reactive = connection.reactive();
        this.expireSecond = cacheConfig.getExpireTime();
        this.prefix = cacheConfig.getPrefix();
        pluginAdoptedCaller = PluginComposer.decorateWithInput(cacheConfig.getPluginInput(), key -> reactive.get(key).map(this::deserialize));
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
        return pluginAdoptedCaller.findOne(this.prefix() + key);
    }


    abstract public byte[] serialize(V v);

    abstract public V deserialize(byte[] bytes);


}
