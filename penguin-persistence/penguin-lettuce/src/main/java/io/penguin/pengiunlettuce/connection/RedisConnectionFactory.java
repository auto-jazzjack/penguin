package io.penguin.pengiunlettuce.connection;

import io.lettuce.core.RedisURI;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.event.DefaultEventPublisherOptions;
import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.DefaultClientResources;
import io.penguin.pengiunlettuce.codec.DefaultCodec;
import io.penguin.pengiunlettuce.codec.LettuceCodec;
import io.penguin.pengiunlettuce.cofig.LettuceCacheConfig;
import io.penguin.pengiunlettuce.connection.channel.CustomNetty;
import io.penguin.penguincore.reader.CacheContext;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RedisConnectionFactory {


    synchronized public static <V> StatefulRedisClusterConnection<String, CacheContext<V>> connection(LettuceResource resource, LettuceCacheConfig<V> lettuceCacheConfig) {


        List<RedisURI> collect = Arrays.stream(resource.getRedisUris().split(","))
                .map(i -> RedisURI.create(i, resource.getPort()))
                .collect(Collectors.toList());
        return connection(collect, new LettuceCodec<>(lettuceCacheConfig.getCodecConfig()));
    }

    public static ClientResources clientResources() {
        return DefaultClientResources.builder()
                .commandLatencyPublisherOptions(DefaultEventPublisherOptions.builder()
                        .eventEmitInterval(Duration.ofMinutes(1))
                        .build())
                .ioThreadPoolSize(Runtime.getRuntime().availableProcessors() * 2)
                .nettyCustomizer(new CustomNetty<>(DefaultCodec.getInstance()))
                .build();
    }

    public static <V> StatefulRedisClusterConnection<String, CacheContext<V>> connection(List<RedisURI> redisURI, RedisCodec<String, CacheContext<V>> codec) {
        RedisClusterClient redisClusterClient = RedisClusterClient.create(clientResources(), redisURI);

        redisClusterClient.setOptions(ClusterClientOptions.builder()
                .topologyRefreshOptions(ClusterTopologyRefreshOptions.builder()
                        .enableAllAdaptiveRefreshTriggers()
                        .enablePeriodicRefresh()
                        .build())
                .requestQueueSize(30000)
                .build());


        return redisClusterClient.connect(codec);
    }
}
