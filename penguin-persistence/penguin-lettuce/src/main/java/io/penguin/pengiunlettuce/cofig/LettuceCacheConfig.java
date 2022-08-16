package io.penguin.pengiunlettuce.cofig;

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
    private String downStreamName;
    private CodecConfig<T> codecConfig;
}
