package io.penguin.pengiunlettuce.connection;

import io.lettuce.core.RedisURI;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.event.DefaultEventPublisherOptions;
import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.DefaultClientResources;
import io.penguin.pengiunlettuce.codec.DefaultCodec;
import io.penguin.pengiunlettuce.connection.channel.CustomNetty;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

public class RedisConnection {

    private static StatefulRedisClusterConnection<String, byte[]> cached;

    synchronized public static StatefulRedisClusterConnection<String, byte[]> connection(LettuceConnectionIngredient ingredient) {

        if (cached != null) {
            return cached;
        }
        List<RedisURI> collect = ingredient.getRedisUris().stream()
                .map(i -> RedisURI.create(i, ingredient.getPort()))
                .collect(Collectors.toList());
        cached = connection(collect);
        return cached;
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

    public static StatefulRedisClusterConnection<String, byte[]> connection(List<RedisURI> redisURI) {
        RedisClusterClient redisClusterClient = RedisClusterClient.create(clientResources(), redisURI);

        redisClusterClient.setOptions(ClusterClientOptions.builder()
                .topologyRefreshOptions(ClusterTopologyRefreshOptions.builder()
                        .enableAllAdaptiveRefreshTriggers()
                        .enablePeriodicRefresh()
                        .build())
                .requestQueueSize(30000)
                .build());


        return redisClusterClient.connect(DefaultCodec.getInstance());
    }
}
