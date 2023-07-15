package io.penguin.penguincodec;


import io.netty.buffer.ByteBuf;

public interface Codec<V> {
    void serialize(V v, ByteBuf buf) throws Exception;

    V deserialize(ByteBuf v) throws Exception;
}
