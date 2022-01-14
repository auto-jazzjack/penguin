package io.penguin.penguincore.config;

import io.lettuce.core.RedisURI;
import io.lettuce.core.TimeoutOptions;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.codec.CompressionCodec;
import io.lettuce.core.event.DefaultEventPublisherOptions;
import io.lettuce.core.metrics.DefaultCommandLatencyCollectorOptions;
import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.DefaultClientResources;
import io.penguin.pengiunlettuce.codec.DefaultCodec;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
public class RedisConfig {

    @Bean
    public StatefulRedisClusterConnection<String, byte[]> statefulRedisClusterConnection() {
        return connection(Stream.of(RedisURI.create("127.0.0.1", 6379))
                .collect(Collectors.toList()));
    }

    private ClientResources clientResources() {
        return DefaultClientResources.builder()
                .commandLatencyPublisherOptions(DefaultEventPublisherOptions.builder()
                        .eventEmitInterval(Duration.ofMinutes(1))
                        .build())
                .ioThreadPoolSize(4)
                .build();
    }

    public StatefulRedisClusterConnection<String, byte[]> connection(List<RedisURI> redisURI) {
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
