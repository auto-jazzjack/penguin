package io.penguin.penguincore.config;

import io.lettuce.core.RedisURI;
import io.lettuce.core.TimeoutOptions;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.codec.CompressionCodec;
import io.penguin.pengiunlettuce.codec.DefaultCodec;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

    public StatefulRedisClusterConnection<String, byte[]> connection(List<RedisURI> redisURI) {
        RedisClusterClient redisClusterClient = RedisClusterClient.create(redisURI);

        redisClusterClient.setOptions(ClusterClientOptions.builder()
                .topologyRefreshOptions(ClusterTopologyRefreshOptions.builder()
                        .enableAllAdaptiveRefreshTriggers()
                        .enablePeriodicRefresh()
                        .build())
                .requestQueueSize(30000)
                .timeoutOptions(TimeoutOptions.builder()
                        .build())
                .build());


        return redisClusterClient.connect(CompressionCodec.valueCompressor(DefaultCodec.getInstance(), CompressionCodec.CompressionType.GZIP));
    }


}
