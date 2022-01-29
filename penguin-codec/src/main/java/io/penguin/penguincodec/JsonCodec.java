package io.penguin.penguincodec;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonCodec<V> extends Codec<V> {

    private final ObjectMapper objectMapper;
    private final Class<V> target;

    public JsonCodec(Class<V> clazz) throws Exception {
        super(clazz);
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
