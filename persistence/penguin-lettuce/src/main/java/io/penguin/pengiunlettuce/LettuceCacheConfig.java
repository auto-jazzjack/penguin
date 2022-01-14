package io.penguin.pengiunlettuce;

import io.lettuce.core.codec.RedisCodec;
import io.penguin.pengiunlettuce.codec.DefaultCodec;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class LettuceCacheConfig {
    private final long expireTime = 0;
    private final RedisCodec<?, byte[]> codec = DefaultCodec.getInstance();
    private final int queueSize = 50000;
}
