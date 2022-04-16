package io.penguin.pengiunlettuce.compress;

import io.penguin.penguincodec.Codec;

public class NoneCompressor<V> extends Compressor<V> {

    public NoneCompressor(Codec<V> delegated) {
        super(delegated);
    }

    @Override
    public byte[] serialize(V source) throws Exception {
        return delegated.serialize(source);
    }

    @Override
    public V deserialize(byte[] source) throws Exception {
        return delegated.deserialize(source);
    }
}
