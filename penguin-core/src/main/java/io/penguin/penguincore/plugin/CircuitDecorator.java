package io.penguin.penguincore.plugin;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import io.micrometer.core.instrument.Counter;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CircuitDecorator<V> {
    private CircuitBreakerOperator<V> circuitBreakerOperator;
    private CircuitBreaker circuitBreaker;
    private Counter success;
    private Counter fail;
}
