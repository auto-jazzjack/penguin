package io.penguin.penguincore.plugin.circuit;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import io.micrometer.core.instrument.Counter;
import io.penguin.penguincore.plugin.CircuitDecorator;
import io.penguin.penguincore.plugin.Plugin;
import reactor.core.publisher.Mono;

public class CircuitPlugn<V> implements Plugin<V> {

    private final CircuitBreakerOperator<V> circuitBreakerOperator;
    private final Counter success;
    private final Counter fail;

    public CircuitPlugn(CircuitDecorator<V> circuitDecorator) {
        this.circuitBreakerOperator = circuitDecorator.getCircuitBreakerOperator();
        this.success = circuitDecorator.getSuccess();
        this.fail = circuitDecorator.getFail();
    }


    @Override
    public Mono<V> decorateSource(Mono<V> source) {
        return ((Mono<V>) this.circuitBreakerOperator.apply(source))
                .doOnError(i -> {
                    if (i instanceof CallNotPermittedException) {
                        fail.increment();
                    }
                })
                .doOnSuccess(i -> success.increment());
    }
}
