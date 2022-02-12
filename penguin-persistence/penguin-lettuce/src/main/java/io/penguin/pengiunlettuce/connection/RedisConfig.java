package io.penguin.pengiunlettuce.connection;

import io.lettuce.core.RedisURI;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.codec.CompressionCodec;
import io.lettuce.core.event.DefaultEventPublisherOptions;
import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.DefaultClientResources;
import io.penguin.pengiunlettuce.codec.DefaultCodec;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

public class RedisConfig {


    public static StatefulRedisClusterConnection<String, byte[]> connection(List<String> redisURIS, int port) {

        List<RedisURI> collect = redisURIS.stream()
                .map(i -> RedisURI.create(i, port))
                .collect(Collectors.toList());
        return connection(collect);
    }

    private static ClientResources clientResources() {
        return DefaultClientResources.builder()
                .commandLatencyPublisherOptions(DefaultEventPublisherOptions.builder()
                        .eventEmitInterval(Duration.ofMinutes(1))
                        .build())
                .ioThreadPoolSize(4)
                .build();
    }

    private static StatefulRedisClusterConnection<String, byte[]> connection(List<RedisURI> redisURI) {
        RedisClusterClient redisClusterClient = RedisClusterClient.create(clientResources(), redisURI);

        redisClusterClient.setOptions(ClusterClientOptions.builder()
                .topologyRefreshOptions(ClusterTopologyRefreshOptions.builder()
                        .enableAllAdaptiveRefreshTriggers()
                        .enablePeriodicRefresh()
                        .build())
                .requestQueueSize(30000)
                .build());


        return redisClusterClient.connect(CompressionCodec.valueCompressor(DefaultCodec.getInstance(), CompressionCodec.CompressionType.GZIP));
    }


}
