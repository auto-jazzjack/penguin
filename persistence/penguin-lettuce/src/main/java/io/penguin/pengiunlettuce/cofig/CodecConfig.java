package io.penguin.pengiunlettuce.cofig;

import io.penguin.penguincodec.Codec;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CodecConfig {
    private Class<Codec> codec;
}
