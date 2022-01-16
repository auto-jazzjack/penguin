package io.penguin.penguincore.plugin.circuit;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import io.penguin.penguincore.plugin.Ingredient.CircuitIngredient;
import io.penguin.penguincore.plugin.Pluggable;
import io.penguin.penguincore.plugin.PluginInput;

import java.time.Duration;
import java.util.Optional;

public class CircuitPluggable extends Pluggable<CircuitIngredient> {

    public CircuitPluggable(PluginInput pluginInput) {
        super(pluginInput);
    }

    @Override
    public boolean support() {

        boolean empty = Optional.ofNullable(pluginInput)
                .map(PluginInput::getCircuit)
                .isEmpty();
        return !empty;
    }

    @Override
    public CircuitIngredient generate() {
        return CircuitIngredient.builder()
                .circuitBreakerOperator(CircuitBreakerOperator.of(CircuitBreaker.of("",
                        CircuitBreakerConfig.custom()
                                .permittedNumberOfCallsInHalfOpenState(pluginInput.getCircuit().getPermittedNumberOfCallsInHalfOpenState())
                                .failureRateThreshold(pluginInput.getCircuit().getFailureRateThreshold())
                                .waitDurationInOpenState(Duration.ofMillis(pluginInput.getCircuit().getWaitDurationInOpenStateMillisecond()))
                                .build())))
                .build();
    }
}
