package io.penguin.penguincore.plugin.Ingredient;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AllIngredient {
    private CircuitIngredient circuitIngredient;
    private TimeoutIngredient timeoutIngredient;
}
