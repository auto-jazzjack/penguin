package io.penguin.penguincore.plugin.Ingredient;

import io.micrometer.core.instrument.Counter;
import io.netty.util.HashedWheelTimer;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TimeoutDecorator {
    private HashedWheelTimer timer;
    private Counter fail;
    private long milliseconds;
}
