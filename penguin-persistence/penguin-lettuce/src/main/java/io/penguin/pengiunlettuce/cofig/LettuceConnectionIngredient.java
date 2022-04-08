package io.penguin.pengiunlettuce.cofig;

import io.penguin.penguincore.plugin.PluginInput;
import io.penguin.penguincore.reader.Reader;
import lombok.Builder;
import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Data
@Builder
public class LettuceConnectionIngredient {

    private long expireMilliseconds;
    private int queueSize;
    private List<String> redisUris;
    private int port;
    private Compression compression;


    private PluginInput pluginInput;

    public static LettuceConnectionIngredient.LettuceConnectionIngredientBuilder base() {
        return LettuceConnectionIngredient.builder()
                .expireMilliseconds(10)
                .queueSize(50000)
                .redisUris(Stream.of("127.0.0.1").collect(Collectors.toList()))
                .port(6379)
                .pluginInput(PluginInput.base().build());
    }

    public static LettuceConnectionIngredient toInternal(LettuceCacheConfig config, Map<String, Reader> readers) {
        Objects.requireNonNull(config);
        LettuceConnectionIngredient build = LettuceConnectionIngredient.base()
                .build();

        Optional.ofNullable(config.getExpireMilliseconds()).ifPresent(build::setExpireMilliseconds);
        Optional.ofNullable(config.getQueueSize()).ifPresent(build::setQueueSize);
        Optional.ofNullable(config.getPort()).ifPresent(build::setPort);
        Optional.ofNullable(config.getRedisUris()).map(i -> Arrays.stream(i.split(",")).collect(Collectors.toList())).ifPresent(build::setRedisUris);
        Optional.ofNullable(config.getCompression()).map(Compression::defaultOrValueOf).ifPresent(build::setCompression);

        return build;
    }

}
