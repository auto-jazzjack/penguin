package io.penguin.pengiunlettuce.compress;

import io.penguin.penguincodec.Codec;

public class GzipCompressor<V> extends Compressor<V> {

    public GzipCompressor(Codec<V> delegated) {
        super(delegated);
    }

    @Override
    public byte[] serialize(V source) {
        return new byte[0];
    }

    @Override
    public V deserialize(byte[] source) {
        return null;
    }
}
