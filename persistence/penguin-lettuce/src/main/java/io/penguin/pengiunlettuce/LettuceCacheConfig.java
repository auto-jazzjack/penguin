package io.penguin.pengiunlettuce;

import io.lettuce.core.codec.RedisCodec;
import io.penguin.pengiunlettuce.codec.DefaultCodec;
import io.penguin.penguincore.plugin.PluginInput;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class LettuceCacheConfig {
    private long expireTime;
    private RedisCodec<?, byte[]> codec;
    private int queueSize;
    private String prefix;

    private PluginInput pluginInput;

    public static LettuceCacheConfig.LettuceCacheConfigBuilder base() {
        return LettuceCacheConfig.builder()
                .expireTime(10)
                .queueSize(50000)
                .codec(DefaultCodec.getInstance())
                .prefix("")
                .pluginInput(PluginInput.base().build());
    }

}
