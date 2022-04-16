package io.penguin.pengiunlettuce.connection;

import lombok.Builder;
import lombok.Data;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Data
@Builder
public class LettuceConnectionIngredient {

    private Long expireMilliseconds;
    private Integer queueSize;
    private List<String> redisUris;
    private Integer port;

    public static LettuceConnectionIngredient.LettuceConnectionIngredientBuilder base() {
        return LettuceConnectionIngredient.builder()
                .expireMilliseconds(10L)
                .queueSize(50000)
                .redisUris(Stream.of("127.0.0.1").collect(Collectors.toList()))
                .port(6379);
    }

    public static LettuceConnectionIngredient toInternal(LettuceConnectionConfig config) {
        Objects.requireNonNull(config);

        return LettuceConnectionIngredient.base()
                .expireMilliseconds(Optional.ofNullable(config.getExpireMilliseconds()).orElse(1000000L))
                .queueSize(Optional.ofNullable(config.getQueueSize()).orElse(30000))
                .port(Optional.ofNullable(config.getPort()).orElse(6379))
                .redisUris(Optional.ofNullable(config.getRedisUris()).map(i -> Arrays.stream(i.split(",")).collect(Collectors.toList()))
                        .orElseThrow(() -> new IllegalArgumentException("Redice host should be supplied")))
                .build();
    }

}
