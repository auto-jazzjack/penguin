package io.penguin.pengiunlettuce.compress;

import io.penguin.penguincodec.Codec;

public abstract class Compressor<V> implements Codec<V> {

    protected Codec<V> delegated;

    public Compressor(Codec<V> delegated) {
        this.delegated = delegated;
    }

    enum Kind {
        NONE,
        GZIP,
    }

    public static Kind kindValueOf(String kind) {
        try {
            return Kind.valueOf(kind);
        } catch (Exception e) {
            return Kind.NONE;
        }
    }
}
