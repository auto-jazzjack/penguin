package io.penguin.penguincore.plugin.circuit;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import io.micrometer.core.instrument.Counter;
import io.penguin.penguincore.plugin.Ingredient.CircuitIngredient;
import io.penguin.penguincore.plugin.Plugin;
import reactor.core.publisher.Mono;

public class CircuitPlugin<V> extends Plugin<V> {

    private final CircuitBreakerOperator<V> circuitBreakerOperator;
    private final Counter success;
    private final Counter fail;

    public CircuitPlugin(CircuitIngredient circuitIngredient) {
        super();
        circuitBreakerOperator = (CircuitBreakerOperator<V>) circuitIngredient.getCircuitBreakerOperator();
        this.success = circuitIngredient.getSuccess();
        this.fail = circuitIngredient.getFail();
    }

    @Override
    public Mono<V> decorateSource(Mono<V> source) {
        this.source = (Mono<V>) this.circuitBreakerOperator.apply(source);
        return this.source
                .doOnError(i -> {
                    if (i instanceof CallNotPermittedException) {
                        fail.increment();
                    }
                })
                .doOnSuccess(i -> success.increment());
    }

}
