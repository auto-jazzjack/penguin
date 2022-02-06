package io.penguin.penguincodec.factory;

import io.penguin.penguincodec.Codec;
import io.penguin.penguincodec.JsonCodec;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class CodecFactoryTest {

    @Test
    public void should_create_JsonCodec() {

        Codec codec = CodecFactory.create(JsonCodec.class, Map.class);
        Assertions.assertEquals(JsonCodec.class, codec.getClass());
    }
}
