package io.penguin.penguincore.plugin.Ingredient;

import io.github.resilience4j.reactor.bulkhead.operator.BulkheadOperator;
import io.micrometer.core.instrument.Counter;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BulkheadIngredient {
    private BulkheadOperator<?> bulkheadOperator;
    private Counter success;
    private Counter fail;
}
