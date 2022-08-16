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
public class CodecConfig<T> {
    private Class<Codec<T>> codec;
    private Class<T> target;
    private String compress;
}
