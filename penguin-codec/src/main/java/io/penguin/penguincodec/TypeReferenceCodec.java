package io.penguin.penguincodec;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TypeReferenceCodec<V> implements Codec<V> {

    private final ObjectMapper objectMapper;
    private final TypeReference<V> target;


    public TypeReferenceCodec(TypeReference<V> clazz) throws Exception {
        objectMapper = new ObjectMapper();
        this.target = clazz;
    }

    @Override
    public byte[] serialize(V v) throws Exception {
        return objectMapper.writeValueAsBytes(v);
    }

    @Override
    public V deserialize(byte[] v) throws Exception {
        return objectMapper.readValue(v, target);
    }
}
