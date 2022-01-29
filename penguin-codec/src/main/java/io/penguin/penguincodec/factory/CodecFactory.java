package io.penguin.penguincodec.factory;

import io.penguin.penguincodec.Codec;
import io.penguin.penguincodec.JsonCodec;
import io.penguin.penguincodec.ProtoCodec;

public class CodecFactory {

    public static Codec create(Class<Codec> clazz, Class args) {

        try {
            if (clazz.equals(JsonCodec.class)) {
                return new JsonCodec(args);
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
