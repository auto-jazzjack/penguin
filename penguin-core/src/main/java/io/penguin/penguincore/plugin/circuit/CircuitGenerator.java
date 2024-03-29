package io.penguin.penguincore.plugin.circuit;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import io.penguin.penguincore.metric.MetricCreator;
import io.penguin.penguincore.plugin.CircuitDecorator;
import io.penguin.penguincore.plugin.PluginGenerator;

import java.time.Duration;

public class CircuitGenerator<V> implements PluginGenerator<CircuitDecorator<V>> {

    private final CircuitModel circuitModel;

    public CircuitGenerator(CircuitModel circuitModel) {
        this.circuitModel = circuitModel;
    }

    static final String fail = "circuit_opened";
    static final String success = "circuit_closed";

    @Override
    public CircuitDecorator<V> generate(Class<?> clazz) {
        CircuitBreaker circuitBreaker = CircuitBreaker.of(this.circuitModel.getCircuitName(), CircuitBreakerConfig.custom()
                .permittedNumberOfCallsInHalfOpenState(this.circuitModel.getPermittedNumberOfCallsInHalfOpenState())
                .failureRateThreshold(this.circuitModel.getFailureRateThreshold())
                .waitDurationInOpenState(Duration.ofMillis(this.circuitModel.getWaitDurationInOpenStateMillisecond()))
                .build());

        return CircuitDecorator.<V>builder()
                .circuitBreakerOperator(CircuitBreakerOperator.of(circuitBreaker))
                .circuitBreaker(circuitBreaker)
                .fail(MetricCreator.counter(fail, "kind", clazz.getSimpleName()))
                .success(MetricCreator.counter(success, "kind", clazz.getSimpleName()))
                .build();
    }
}
