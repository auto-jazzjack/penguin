package io.penguin.penguincodec;

import com.google.protobuf.Message;
import com.google.protobuf.Parser;

import java.lang.reflect.Method;

public class ProtoCodec<V extends Message> implements Codec<V> {

    private final Parser<? extends V> parserForType;

    public ProtoCodec(Class<V> clazz) throws Exception {
        Method newBuilder = clazz.getDeclaredMethod("newBuilder");
        newBuilder.setAccessible(true);
        Message.Builder builder = (Message.Builder) newBuilder.invoke(clazz);

        parserForType = (Parser<? extends V>) clazz.getDeclaredMethod("getParserForType")
                .invoke(builder.build());
    }

    @Override
    public byte[] serialize(V v) throws Exception {
        return v.toByteArray();
    }

    @Override
    public V deserialize(byte[] v) throws Exception {
        return parserForType.parseFrom(v);
    }
}
