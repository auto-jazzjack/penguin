package io.penguin.penguincore.plugin.decorator;

import lombok.*;

@Data
@Builder
public class Decorators {
    private CircuitDecorator circuitDecorator;
    private BulkheadDecorator bulkheadDecorator;
    private TimeoutDecorator timeoutDecorator;


}
