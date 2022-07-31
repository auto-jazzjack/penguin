package io.penguin.pengiunlettuce.cofig;

import io.lettuce.core.codec.RedisCodec;
import io.penguin.pengiunlettuce.codec.DefaultCodec;
import io.penguin.pengiunlettuce.compress.Compressor;
import io.penguin.pengiunlettuce.compress.CompressorFactory;
import io.penguin.penguincodec.Codec;
import io.penguin.penguincodec.factory.CodecFactory;
import io.penguin.penguincore.plugin.PluginInput;
import io.penguin.penguincore.reader.Context;
import io.penguin.penguincore.reader.Reader;
import lombok.Builder;
import lombok.Data;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;


@Data
@Builder
@SuppressWarnings({"unchecked", "rawtypes"})
public class LettuceCacheIngredient {

    private RedisCodec<?, byte[]> connectionCodec;
    private String prefix;
    private Reader fromDownStream;
    private Codec codec;

    private PluginInput pluginInput;

    public static LettuceCacheIngredient.LettuceCacheIngredientBuilder base() {
        return LettuceCacheIngredient.builder()
                .connectionCodec(DefaultCodec.getInstance())
                .pluginInput(PluginInput.base().build());
    }

    public static LettuceCacheIngredient toInternal(LettuceCacheConfig config, Map<String, Reader<Object, Context<Object>>> readers) {
        Objects.requireNonNull(config);
        LettuceCacheIngredient build = LettuceCacheIngredient.base()
                .build();

        Optional.ofNullable(config.getPrefix()).ifPresent(build::setPrefix);
        Optional.ofNullable(config.getDownStreamName()).ifPresent(i -> build.setFromDownStream(readers.get(i)));
        Optional.ofNullable(config.getCodecConfig()).ifPresent(i -> {
            Codec codec = CodecFactory.create(i.getCodec(), i.getTarget());
            build.setCodec(CompressorFactory.generate(Compressor.kindValueOf(i.getCompress()), codec));
        });

        return build;
    }


}
