package io.penguin.pengiunlettuce;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class LettuceCacheConfig {
    private final long expireTime = 0;
}
