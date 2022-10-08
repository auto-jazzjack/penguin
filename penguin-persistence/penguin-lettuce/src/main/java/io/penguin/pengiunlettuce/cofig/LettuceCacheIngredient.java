package io.penguin.pengiunlettuce.cofig;

import io.lettuce.core.codec.RedisCodec;
import io.penguin.pengiunlettuce.codec.DefaultCodec;
import io.penguin.pengiunlettuce.compress.Compressor;
import io.penguin.pengiunlettuce.compress.CompressorFactory;
import io.penguin.penguincodec.Codec;
import io.penguin.penguincodec.factory.CodecFactory;
import io.penguin.penguincore.plugin.PluginInput;
import io.penguin.penguincore.reader.CacheContext;
import io.penguin.penguincore.reader.Reader;
import lombok.Builder;
import lombok.Data;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;


@Data
@Builder
public class LettuceCacheIngredient<K, V> {

    private RedisCodec<?, byte[]> connectionCodec;
    private String prefix;
    private Reader<K, V> fromDownStream;
    private Codec<V> codec;

    private PluginInput pluginInput;

    public static <K_, V_> LettuceCacheIngredient.LettuceCacheIngredientBuilder<K_, V_> base() {
        return LettuceCacheIngredient.<K_, V_>builder()
                .connectionCodec(DefaultCodec.getInstance())
                .pluginInput(PluginInput.base().build());
    }

    public static <K_, V_> LettuceCacheIngredient<K_, V_> toInternal(LettuceCacheConfig<V_> config, Map<String, Reader<K_, CacheContext<V_>>> readers) {
        Objects.requireNonNull(config);
        LettuceCacheIngredient<K_, V_> build = LettuceCacheIngredient.<K_, V_>base()
                .build();

        Optional.ofNullable(config.getPrefix()).ifPresent(build::setPrefix);
        Optional.ofNullable(config.getDownStreamName()).ifPresent(i -> build.setFromDownStream(readers.get(i)));

        Optional.ofNullable(config.getCodecConfig())
                .ifPresent(i -> {
                    Codec<V_> codec = CodecFactory.create(i.getCodec(), i.getTarget());
                    build.setCodec(CompressorFactory.generate(Compressor.kindValueOf(i.getCompress()), codec));
                });

        return build;
    }


}
