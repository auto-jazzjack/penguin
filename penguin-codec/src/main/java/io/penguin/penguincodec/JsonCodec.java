package io.penguin.penguincodec;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;

import java.io.InputStream;

public class JsonCodec<V> implements Codec<V> {

    private final ObjectMapper objectMapper;
    private final Class<V> target;


    public JsonCodec(Class<V> clazz) throws Exception {
        objectMapper = new ObjectMapper();
        this.target = clazz;
    }

    @Override
    public void serialize(V v, ByteBuf buf) throws Exception {
        buf.writeBytes(objectMapper.writeValueAsBytes(v));
    }

    @Override
    public V deserialize(ByteBuf buf) throws Exception {
        return objectMapper.readValue((InputStream) new ByteBufInputStream(buf), target);
    }
}
