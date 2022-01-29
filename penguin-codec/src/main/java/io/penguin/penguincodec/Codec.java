package io.penguin.penguincodec;

public abstract class Codec<V> {

    public abstract byte[] serialize(V v) throws Exception;

    public abstract V deserialize(byte[] v) throws Exception;

    public Codec(Class<V> target) throws Exception {

    }
}
