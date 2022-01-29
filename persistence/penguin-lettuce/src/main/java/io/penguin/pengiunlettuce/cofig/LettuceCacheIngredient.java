package io.penguin.pengiunlettuce.cofig;

import io.lettuce.core.codec.RedisCodec;
import io.penguin.pengiunlettuce.codec.DefaultCodec;
import io.penguin.penguincodec.Codec;
import io.penguin.penguincodec.factory.CodecFactory;
import io.penguin.penguincore.plugin.PluginInput;
import io.penguin.penguincore.reader.Reader;
import lombok.Builder;
import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Data
@Builder
public class LettuceCacheIngredient {

    private long expireMilliseconds;
    private RedisCodec<?, byte[]> connectionCodec;
    private int queueSize;
    private String prefix;
    private List<String> redisUris;
    private int port;
    private Reader fromDownStream;
    private Codec codec;

    private PluginInput pluginInput;

    public static LettuceCacheIngredient.LettuceCacheIngredientBuilder base() {
        return LettuceCacheIngredient.builder()
                .expireMilliseconds(10)
                .queueSize(50000)
                .connectionCodec(DefaultCodec.getInstance())
                .redisUris(Stream.of("127.0.0.1").collect(Collectors.toList()))
                .port(6379)
                .pluginInput(PluginInput.base().build());
    }

    public static LettuceCacheIngredient toInternal(LettuceCacheConfig config, Map<String, Reader> readers) {
        Objects.requireNonNull(config);
        LettuceCacheIngredient build = LettuceCacheIngredient.base()
                .build();

        Optional.ofNullable(config.getExpireMilliseconds()).ifPresent(build::setExpireMilliseconds);
        Optional.ofNullable(config.getQueueSize()).ifPresent(build::setQueueSize);
        Optional.ofNullable(config.getPort()).ifPresent(build::setPort);
        Optional.ofNullable(config.getPrefix()).ifPresent(build::setPrefix);
        Optional.ofNullable(config.getRedisUris()).map(i -> Arrays.stream(i.split(",")).collect(Collectors.toList())).ifPresent(build::setRedisUris);
        Optional.ofNullable(config.getDownStreamName()).ifPresent(i -> build.setFromDownStream(readers.get(i)));
        Optional.ofNullable(config.getCodecConfig()).ifPresent(i -> build.setCodec(CodecFactory.create(i.getCodec(), i.getTarget())));


        return build;
    }

}
