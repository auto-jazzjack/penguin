package io.penguin.penguincodec;


public interface Codec<V> {
    byte[] serialize(V v) throws Exception;

    V deserialize(byte[] v) throws Exception;
}
