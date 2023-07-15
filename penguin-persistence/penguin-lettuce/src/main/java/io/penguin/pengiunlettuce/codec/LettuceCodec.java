package io.penguin.pengiunlettuce.codec;

import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.ToByteBufEncoder;
import io.netty.buffer.*;
import io.penguin.pengiunlettuce.cofig.LettuceCacheConfig;
import io.penguin.penguincodec.Codec;
import io.penguin.penguincore.reader.CacheContext;

import java.lang.reflect.Constructor;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import static io.penguin.pengiunlettuce.codec.DefaultCodec.toBytes;

public class LettuceCodec<V> implements RedisCodec<String, CacheContext<V>>, ToByteBufEncoder<String, CacheContext<V>> {

    private final Codec<V> codec;

    public LettuceCodec(LettuceCacheConfig.CodecConfig<V> codecConfig) {
        this.codec = createInstance(codecConfig.getCodec(), codecConfig.getTargetClass());
    }

    Codec<V> createInstance(Class<Codec<V>> codec, Class<V> target) {
        try {
            Constructor<Codec<V>> constructor = codec.getConstructor(Class.class);
            return constructor.newInstance(target);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String decodeKey(ByteBuffer bytes) {
        return new String(toBytes(bytes), StandardCharsets.UTF_8);
    }

    @Override
    public CacheContext<V> decodeValue(ByteBuffer byteBuffer) {
        try {
            ByteBuf byteBuf = Unpooled.wrappedBuffer(byteBuffer);
            long timeStamp = byteBuf.readLong();
            V deserialize = this.codec.deserialize(byteBuf);
            return new CacheContext<>() {
                @Override
                public V getValue() {
                    return deserialize;
                }

                @Override
                public long getTimeStamp() {
                    return timeStamp;
                }
            };

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ByteBuffer encodeKey(String key) {
        return ByteBuffer.wrap(key.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public ByteBuffer encodeValue(CacheContext<V> value) {
        try {
            ByteBuf byteBuf = ByteBufAllocator.DEFAULT.directBuffer(estimateSize(value.getValue()));
            byteBuf.writeLong(value.getTimeStamp());
            codec.serialize(value.getValue(), byteBuf);
            return byteBuf.nioBuffer();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void encodeKey(String s, ByteBuf byteBuf) {
        byteBuf.writeCharSequence(s, StandardCharsets.UTF_8);
    }

    @Override
    public void encodeValue(CacheContext<V> value, ByteBuf byteBuf) {
        try {
            byteBuf.writeLong(value.getTimeStamp());
            this.codec.serialize(value.getValue(), byteBuf);
        } catch (Exception e) {

        }

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
        return 1;
    }
}
