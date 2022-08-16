package io.penguin.pengiunlettuce.connection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LettuceConnectionConfig {
    private Long expireMilliseconds;
    private Integer queueSize;
    private Integer port;
    private String redisUris;
    private String compression;
}
