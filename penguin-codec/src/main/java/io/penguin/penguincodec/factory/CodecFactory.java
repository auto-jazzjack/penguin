package io.penguin.penguincodec.factory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.protobuf.Message;
import io.penguin.penguincodec.Codec;
import io.penguin.penguincodec.JsonCodec;
import io.penguin.penguincodec.ProtoCodec;
import io.penguin.penguincodec.TypeReferenceCodec;

public class CodecFactory {

    public static <T> Codec<T> create(Class<? extends Codec<T>> clazz, TypeReference<T> args) {

        try {
            if (clazz.equals(JsonCodec.class) || clazz.equals(TypeReferenceCodec.class)) {
                return new TypeReferenceCodec<>(args);
            }

            throw new IllegalStateException("No such codec");
        } catch (Exception e) {
            throw new IllegalStateException("Cannot create Codec");
        }
    }

    public static <T> Codec<T> create(Class<? extends Codec<T>> clazz, Class<T> args) {

        try {
            if (clazz.equals(JsonCodec.class)) {
                return new JsonCodec<>(args);
            }
            if (clazz.equals(ProtoCodec.class)) {
                return new ProtoCodec(args);
            }
            throw new IllegalStateException("No such codec");
        } catch (Exception e) {
            throw new IllegalStateException("Cannot create Codec");
        }
    }
}
