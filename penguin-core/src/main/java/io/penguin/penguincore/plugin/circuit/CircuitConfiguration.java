package io.penguin.penguincore.plugin.circuit;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import io.penguin.penguincore.metric.MetricCreator;
import io.penguin.penguincore.plugin.Ingredient.CircuitIngredient;
import io.penguin.penguincore.plugin.PluginConfiguration;
import io.penguin.penguincore.plugin.PluginInput;

import java.time.Duration;
import java.util.Optional;

public class CircuitConfiguration extends PluginConfiguration<CircuitIngredient> {

    public CircuitConfiguration(PluginInput pluginInput) {
        super(pluginInput);
    }

    @Override
    public boolean support() {

        boolean empty = Optional.ofNullable(pluginInput)
                .map(PluginInput::getCircuit)
                .isEmpty();
        return !empty;
    }


    private static final String fail = "circuit_opened";
    private static final String success = "circuit_closed";

    @Override
    public CircuitIngredient generate(Class<?> clazz) {
        CircuitBreaker circuitBreaker = CircuitBreaker.of(pluginInput.getCircuit().getCircuitName(), CircuitBreakerConfig.custom()
                .permittedNumberOfCallsInHalfOpenState(pluginInput.getCircuit().getPermittedNumberOfCallsInHalfOpenState())
                .failureRateThreshold(pluginInput.getCircuit().getFailureRateThreshold())
                .waitDurationInOpenState(Duration.ofMillis(pluginInput.getCircuit().getWaitDurationInOpenStateMillisecond()))
                .build());

        return CircuitIngredient.builder()
                .circuitBreakerOperator(CircuitBreakerOperator.of(circuitBreaker))
                .circuitBreaker(circuitBreaker)
                .fail(MetricCreator.counter(fail, "kind", clazz.getSimpleName()))
                .success(MetricCreator.counter(success, "kind", clazz.getSimpleName()))
                .build();
    }
}
