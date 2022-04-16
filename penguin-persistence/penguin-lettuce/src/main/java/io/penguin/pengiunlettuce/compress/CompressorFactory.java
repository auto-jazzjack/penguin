package io.penguin.pengiunlettuce.compress;

import io.lettuce.core.codec.RedisCodec;
import io.penguin.penguincodec.Codec;

public class CompressorFactory {

    /**
     * This method create compress enabled Codec.
     */
    public static <V> Codec<V> generate(Compressor.Kind kind, Codec<V> delegated) {
        switch (kind) {
            case NONE:
                return delegated;
            case GZIP:
                return new GzipCompressor<>(delegated);
        }
        return delegated;
    }
}
