package io.penguin.penguincore.plugin.Ingredient;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CircuitIngredient {
    private CircuitBreakerOperator<?> circuitBreakerOperator;
    private CircuitBreaker circuitBreaker;
}
