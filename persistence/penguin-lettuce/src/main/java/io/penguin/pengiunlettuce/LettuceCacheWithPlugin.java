package io.penguin.pengiunlettuce;

import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.penguin.penguincore.plugin.PluginComposer;
import io.penguin.penguincore.plugin.PluginInput;
import io.penguin.penguincore.reader.Reader;
import reactor.core.publisher.Mono;

public abstract class LettuceCacheWithPlugin<K, V> extends RawLettuceCache<K, V> {

    private final Reader<K, V> pluginAdoptedCaller;

    public LettuceCacheWithPlugin(Reader<K, V> fromDownStream, StatefulRedisClusterConnection<K, byte[]> connection, LettuceCacheConfig lettuceCacheConfig, PluginInput pluginInput) throws Exception {
        super(fromDownStream, connection, lettuceCacheConfig);
        pluginAdoptedCaller = PluginComposer.decorateWithInput(pluginInput, this);

    }

    @Override
    public Mono<V> findOne(K key) {
        return pluginAdoptedCaller.findOne(key);
    }
}
