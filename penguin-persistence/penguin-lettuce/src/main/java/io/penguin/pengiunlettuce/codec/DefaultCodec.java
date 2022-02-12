package io.penguin.pengiunlettuce.codec;

import io.lettuce.core.codec.RedisCodec;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class DefaultCodec implements RedisCodec<String, byte[]> {

    public static DefaultCodec instance;

    synchronized public static DefaultCodec getInstance() {
        if (instance == null) {
            instance = new DefaultCodec();
        }

        return instance;
    }

    private DefaultCodec() {

    }

    @Override
    public String decodeKey(ByteBuffer bytes) {
        return new String(toBytes(bytes), StandardCharsets.UTF_8);
    }

    @Override
    public byte[] decodeValue(ByteBuffer bytes) {
        return toBytes(bytes);
    }

    @Override
    public ByteBuffer encodeKey(String key) {
        return ByteBuffer.wrap(key.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public ByteBuffer encodeValue(byte[] value) {
        return ByteBuffer.wrap(value);
    }

    static byte[] toBytes(ByteBuffer byteBuffer) {
        byte[] retv = new byte[byteBuffer.remaining()];
        byteBuffer.get(retv);
        return retv;
    }
}
