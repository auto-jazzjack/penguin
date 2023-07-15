package io.penguin.pengiunlettuce.cofig;

import io.penguin.penguincodec.Codec;
import io.penguin.penguincore.plugin.circuit.CircuitModel;
import io.penguin.penguincore.plugin.timeout.TimeoutModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LettuceCacheConfig<T> {
    private String prefix;
    private Integer expireMilliseconds;
    private String downStreamName;
    private CodecConfig<T> codecConfig;

    private CircuitModel circuit;
    private TimeoutModel timeout;

    @Data
    @NoArgsConstructor
    public static class CodecConfig<T> {
        private Class<Codec<T>> codec;
        private Class<T> targetClass;
    }


}
