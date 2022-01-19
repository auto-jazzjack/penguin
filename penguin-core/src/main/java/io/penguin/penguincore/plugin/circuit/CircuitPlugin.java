package io.penguin.penguincore.plugin.circuit;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import io.penguin.penguincore.plugin.Ingredient.AllIngredient;
import io.penguin.penguincore.plugin.Plugin;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.Mono;

public class CircuitPlugin<V> extends Plugin<V> {

    private final CircuitBreakerOperator<V> circuitBreakerOperator;
    private final CircuitBreaker circuitBreaker;

    public CircuitPlugin(Mono<V> source, AllIngredient allIngredient) {
        super(source, allIngredient);
        circuitBreakerOperator = (CircuitBreakerOperator<V>) allIngredient.getCircuitIngredient().getCircuitBreakerOperator();
        circuitBreaker = allIngredient.getCircuitIngredient().getCircuitBreaker();
        this.source = (Mono<V>) circuitBreakerOperator.apply(source);
    }


    @Override
    public void subscribe(CoreSubscriber<? super V> actual) {
        System.out.println(circuitBreaker.getState());
        source.subscribe(actual);
    }
}
