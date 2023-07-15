package io.penguin.pengiunlettuce.codec;

import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.ToByteBufEncoder;
import io.netty.buffer.ByteBuf;
import io.penguin.penguincodec.Codec;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import static io.penguin.pengiunlettuce.codec.DefaultCodec.toBytes;

public class LettuceCodec<V> implements RedisCodec<String, V>, ToByteBufEncoder<String, V> {


    //private final Codec<V> codec;

    public LettuceCodec(Class<Codec<V>> codec) {
        //this.codec = codec;
    }

    @Override
    public String decodeKey(ByteBuffer bytes) {
        return new String(toBytes(bytes), StandardCharsets.UTF_8);
    }

    @Override
    public V decodeValue(ByteBuffer bytes) {
        //this.codec.deserialize()
        //return toBytes(bytes);
        return null;
    }

    @Override
    public ByteBuffer encodeKey(String key) {
        return ByteBuffer.wrap(key.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public ByteBuffer encodeValue(V value) {
        return null;
    }


    @Override
    public void encodeKey(String s, ByteBuf byteBuf) {
        byteBuf.writeCharSequence(s, StandardCharsets.UTF_8);
    }

    @Override
    public void encodeValue(V bytes, ByteBuf byteBuf) {

    }

    @Override
    public int estimateSize(Object o) {
        if (o instanceof String) {
            String v = (String) o;
            return v.length();
        } else if (o instanceof byte[]) {
            byte[] v = (byte[]) o;
            return v.length;
        }
        return 0;
    }
}
