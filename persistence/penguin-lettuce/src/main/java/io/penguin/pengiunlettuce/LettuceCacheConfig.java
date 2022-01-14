package io.penguin.pengiunlettuce;

import io.lettuce.core.codec.RedisCodec;
import io.penguin.pengiunlettuce.codec.DefaultCodec;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class LettuceCacheConfig {
    private long expireTime = 0;
    private RedisCodec<?, byte[]> codec = DefaultCodec.getInstance();
    private int queueSize = 50000;
}
