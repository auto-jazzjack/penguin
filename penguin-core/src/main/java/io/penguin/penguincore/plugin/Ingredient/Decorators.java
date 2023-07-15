package io.penguin.penguincore.plugin.Ingredient;

import lombok.*;

@Data
@Builder
public class Decorators {
    private CircuitDecorator circuitDecorator;
    private BulkheadDecorator bulkheadDecorator;
    private TimeoutDecorator timeoutDecorator;


}
