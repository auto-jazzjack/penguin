package io.penguin.pengiunlettuce.cofig;

import io.penguin.pengiunlettuce.compress.Compressor;
import io.penguin.pengiunlettuce.compress.CompressorFactory;
import io.penguin.penguincodec.Codec;
import io.penguin.penguincodec.factory.CodecFactory;
import io.penguin.penguincore.plugin.PluginInput;
import lombok.Builder;
import lombok.Data;

import java.util.Objects;
import java.util.Optional;

@Data
@Builder
public class LettuceCacheIngredient<V> {

    private String prefix;
    private Codec<V> codec;

    private PluginInput pluginInput;

    public static <V_> LettuceCacheIngredient.LettuceCacheIngredientBuilder<V_> base() {
        return LettuceCacheIngredient.<V_>builder()
                .pluginInput(PluginInput.base().build());
    }

    public static <V_> LettuceCacheIngredient<V_> toInternal(LettuceCacheConfig<V_> config) {
        Objects.requireNonNull(config);
        LettuceCacheIngredient<V_> build = LettuceCacheIngredient.<V_>base()
                .build();

        Optional.ofNullable(config.getPrefix()).ifPresent(build::setPrefix);

        Optional.ofNullable(config.getCodecConfig())
                .ifPresent(i -> {
                    Codec<V_> codec = CodecFactory.create(i.getCodec(), i.getTarget());
                    build.setCodec(CompressorFactory.generate(Compressor.kindValueOf(i.getCompress()), codec));
                });

        return build;
    }


}
