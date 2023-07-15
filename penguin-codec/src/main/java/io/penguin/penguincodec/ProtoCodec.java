package io.penguin.penguincodec;

import com.google.protobuf.Message;
import com.google.protobuf.Parser;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;

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
    public void serialize(V v, ByteBuf buf) throws Exception {
        buf.writeBytes(v.toByteArray());
    }

    @Override
    public V deserialize(ByteBuf v) throws Exception {
        return parserForType.parseFrom(new ByteBufInputStream(v));
    }
}
