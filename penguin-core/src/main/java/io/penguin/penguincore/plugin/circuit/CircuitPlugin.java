package io.penguin.penguincore.plugin.circuit;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import io.penguin.penguincore.plugin.Plugin;
import io.penguin.penguincore.plugin.PluginInput;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Objects;
import java.util.Optional;

public class CircuitPlugin<V> extends Plugin<V> {

    private final CircuitBreakerOperator<V> circuitBreakerOperator;

    public CircuitPlugin(PluginInput pluginInput, Mono<V> source) {
        super(pluginInput, source);
        Objects.requireNonNull(pluginInput);
        Objects.requireNonNull(pluginInput.getCircuit());

        circuitBreakerOperator = CircuitBreakerOperator.of(CircuitBreaker.of("",
                CircuitBreakerConfig.custom()
                        .permittedNumberOfCallsInHalfOpenState(pluginInput.getCircuit().getPermittedNumberOfCallsInHalfOpenState())
                        .failureRateThreshold(pluginInput.getCircuit().getFailureRateThreshold())
                        .waitDurationInOpenState(Duration.ofMillis(pluginInput.getCircuit().getWaitDurationInOpenStateMillisecond()))
                        .build()));
    }

    @Override
    public int order() {
        return super.pluginInput.getCircuit().getOrder();
    }

    @Override
    public boolean support() {

        boolean empty = Optional.ofNullable(pluginInput)
                .map(PluginInput::getCircuit)
                .isEmpty();

        return !empty;
    }

    @Override
    public Publisher<V> apply() {
        return circuitBreakerOperator.apply(this.source);
    }
}
