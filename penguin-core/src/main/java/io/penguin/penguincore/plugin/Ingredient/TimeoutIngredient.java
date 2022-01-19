package io.penguin.penguincore.plugin.Ingredient;

import io.netty.util.HashedWheelTimer;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TimeoutIngredient {
    private HashedWheelTimer timer;
    private long milliseconds;
}
