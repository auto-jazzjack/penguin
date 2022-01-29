package io.penguin.penguincodec.factory;

import io.penguin.penguincodec.Codec;

public class CodecFactory {

    public static Codec create(Class<Codec> clazz, Object... args) {

        try {

            Class[] classes = new Class[args.length];
            for (int j = 0; j < args.length; j++) {
                classes[j] = args[j].getClass();
            }

            return clazz.getDeclaredConstructor(classes)
                    .newInstance(args);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot create Codec");
        }
    }
}
