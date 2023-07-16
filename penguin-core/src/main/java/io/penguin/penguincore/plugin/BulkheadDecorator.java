package io.penguin.penguincore.plugin;

import io.github.resilience4j.reactor.bulkhead.operator.BulkheadOperator;
import io.micrometer.core.instrument.Counter;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BulkheadDecorator<V> {
    private BulkheadOperator<V> bulkheadOperator;
    private Counter success;
    private Counter fail;
}
