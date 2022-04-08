package io.penguin.pengiunlettuce.cofig;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


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
