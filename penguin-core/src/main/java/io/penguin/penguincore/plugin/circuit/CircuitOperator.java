package io.penguin.penguincore.plugin.circuit;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import io.micrometer.core.instrument.Counter;
import io.penguin.penguincore.plugin.CircuitDecorator;
import org.reactivestreams.Publisher;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.Mono;

public class CircuitOperator<V> extends Mono<V> {

    private final CircuitBreakerOperator<V> circuitBreakerOperator;
    private final Counter success;
    private final Counter fail;

    public CircuitOperator(CircuitDecorator circuitDecorator) {
        super();
        circuitBreakerOperator = (CircuitBreakerOperator<V>) circuitDecorator.getCircuitBreakerOperator();
        this.success = circuitDecorator.getSuccess();
        this.fail = circuitDecorator.getFail();
    }


    @Override
    public void subscribe(CoreSubscriber<? super V> actual) {
        ((Mono<V>) this.circuitBreakerOperator.apply((Publisher<V>) actual))
                .doOnError(i -> {
                    if (i instanceof CallNotPermittedException) {
                        fail.increment();
                    }
                })
                .doOnSuccess(i -> success.increment());
    }
}
