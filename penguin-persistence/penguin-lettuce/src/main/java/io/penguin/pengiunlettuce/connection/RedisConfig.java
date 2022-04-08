package io.penguin.pengiunlettuce.connection;

import io.lettuce.core.RedisURI;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.codec.CompressionCodec;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.event.DefaultEventPublisherOptions;
import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.DefaultClientResources;
import io.penguin.pengiunlettuce.codec.DefaultCodec;
import io.penguin.pengiunlettuce.cofig.LettuceCacheIngredient;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

public class RedisConfig {


    public static StatefulRedisClusterConnection<String, byte[]> connection(LettuceCacheIngredient ingredient) {

        List<RedisURI> collect = ingredient.getRedisUris().stream()
                .map(i -> RedisURI.create(i, ingredient.getPort()))
                .collect(Collectors.toList());
        return connection(ingredient, collect);
    }

    private static ClientResources clientResources() {
        return DefaultClientResources.builder()
                .commandLatencyPublisherOptions(DefaultEventPublisherOptions.builder()
                        .eventEmitInterval(Duration.ofMinutes(1))
                        .build())
                .ioThreadPoolSize(Runtime.getRuntime().availableProcessors() * 2)
                .build();
    }

    private static StatefulRedisClusterConnection<String, byte[]> connection(LettuceCacheIngredient ingredient, List<RedisURI> redisURI) {
        RedisClusterClient redisClusterClient = RedisClusterClient.create(clientResources(), redisURI);

        redisClusterClient.setOptions(ClusterClientOptions.builder()
                .topologyRefreshOptions(ClusterTopologyRefreshOptions.builder()
                        .enableAllAdaptiveRefreshTriggers()
                        .enablePeriodicRefresh()
                        .build())
                .requestQueueSize(30000)
                .build());


        RedisCodec<String, byte[]> codec;
        switch (ingredient.getCompression()) {
            case GZIP:
                codec = CompressionCodec.valueCompressor(DefaultCodec.getInstance(), CompressionCodec.CompressionType.GZIP);
                break;
            case DEFLATE:
                codec = CompressionCodec.valueCompressor(DefaultCodec.getInstance(), CompressionCodec.CompressionType.DEFLATE);
                break;
            case NONE:
            default:
                codec = DefaultCodec.getInstance();
        }

        return redisClusterClient.connect(codec);
    }


}
