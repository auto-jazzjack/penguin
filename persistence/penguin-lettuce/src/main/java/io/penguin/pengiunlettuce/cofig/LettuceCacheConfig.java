package io.penguin.pengiunlettuce.cofig;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LettuceCacheConfig {
    private Long expireMilliseconds;
    private Integer queueSize;
    private String prefix;
    private List<String> redisUris;
    private Integer port;
    private String downStreamName;
    private CodecConfig codecConfig;
}
