package io.penguin.penguincodec;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;

public class TypeReferenceCodec<V> implements Codec<V> {

    private final ObjectMapper objectMapper;
    private final TypeReference<V> target;


    public TypeReferenceCodec(TypeReference<V> clazz) throws Exception {
        objectMapper = new ObjectMapper();
        this.target = clazz;
    }

    @Override
    public void serialize(V v, ByteBuf buf) throws Exception {
        buf.writeBytes(objectMapper.writeValueAsBytes(v));
    }

    @Override
    public V deserialize(ByteBuf buf) throws Exception {
        return objectMapper.readValue(new ByteBufInputStream(buf), target);
    }
}
