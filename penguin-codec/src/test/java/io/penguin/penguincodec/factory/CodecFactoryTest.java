package io.penguin.penguincodec.factory;

import com.fasterxml.jackson.core.type.TypeReference;
import io.penguin.penguincodec.Codec;
import io.penguin.penguincodec.JsonCodec;
import io.penguin.penguincodec.TypeReferenceCodec;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class CodecFactoryTest {

    @Test
    public void should_create_JsonCodec() throws Exception {

        Class<? extends JsonCodec> jsonCodecClass = JsonCodec.class;
        Class<? extends JsonCodec<String>> stringCodec = (Class<? extends JsonCodec<String>>) jsonCodecClass;
        Codec<String> codec = CodecFactory.create(stringCodec, String.class);
        Assertions.assertEquals(JsonCodec.class, codec.getClass());
    }

    @Test
    public void should_create_TypeRef() {

        Class<? extends JsonCodec> jsonCodecClass = JsonCodec.class;
        Class<JsonCodec<Map<String, String>>> jsonCodecClass1 = (Class<JsonCodec<Map<String, String>>>) jsonCodecClass;
        Codec<Map<String, String>> codec = CodecFactory.create(jsonCodecClass1, new TypeReference<>() {
        });
        Assertions.assertEquals(TypeReferenceCodec.class, codec.getClass());
    }
}
