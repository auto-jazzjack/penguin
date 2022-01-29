package io.penguin.pengiunlettuce.cofig;

import io.penguin.penguincodec.Codec;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodecConfig {
    private Class<Codec> codec;
}
