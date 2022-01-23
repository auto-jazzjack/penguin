package io.penguin.penguincore.plugin.circuit;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import io.micrometer.core.instrument.Counter;
import io.penguin.penguincore.plugin.Ingredient.AllIngredient;
import io.penguin.penguincore.plugin.Plugin;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.Mono;

public class CircuitPlugin<V> extends Plugin<V> {

    private final CircuitBreaker circuitBreaker;
    private final Counter success;
    private final Counter fail;

    public CircuitPlugin(Mono<V> source, AllIngredient allIngredient) {
        super(source, allIngredient);
        CircuitBreakerOperator<V> circuitBreakerOperator = (CircuitBreakerOperator<V>) allIngredient.getCircuitIngredient().getCircuitBreakerOperator();
        circuitBreaker = allIngredient.getCircuitIngredient().getCircuitBreaker();

        this.source = (Mono<V>) circuitBreakerOperator.apply(source);
        this.success = allIngredient.getCircuitIngredient().getSuccess();
        this.fail = allIngredient.getCircuitIngredient().getFail();
    }


    @Override
    public void subscribe(CoreSubscriber<? super V> actual) {
        source.doOnError(i -> {
                    if (i instanceof CallNotPermittedException) {
                        fail.increment();
                    }
                })
                .doOnSuccess(i -> success.increment())
                .subscribe(actual);
    }
}
