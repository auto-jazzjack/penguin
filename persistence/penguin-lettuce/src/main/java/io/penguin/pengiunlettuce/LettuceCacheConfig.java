package io.penguin.pengiunlettuce;

import io.lettuce.core.codec.RedisCodec;
import io.penguin.core.cache.penguin;
import io.penguin.pengiunlettuce.codec.DefaultCodec;
import io.penguin.penguincodec.Codec;
import io.penguin.penguincore.plugin.PluginInput;
import io.penguin.penguincore.reader.Reader;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Data
@Builder
public class LettuceCacheConfig {
    private long expireMilliseconds;
    private RedisCodec<?, byte[]> codec;
    private int queueSize;
    private String prefix;
    private List<String> redisUris;
    private int port;
    private Reader fromDownStream;
    private Codec objectCodec;

    private PluginInput pluginInput;

    public static LettuceCacheConfig.LettuceCacheConfigBuilder base() {
        return LettuceCacheConfig.builder()
                .expireMilliseconds(10)
                .queueSize(50000)
                .codec(DefaultCodec.getInstance())
                .prefix("")
                .redisUris(Stream.of("127.0.0.1").collect(Collectors.toList()))
                .port(6379)
                .pluginInput(PluginInput.base().build());
    }

}
