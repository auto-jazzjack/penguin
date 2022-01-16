package io.penguin.penguincore.plugin.circuit;

import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import io.penguin.penguincore.plugin.Ingredient.AllIngredient;
import io.penguin.penguincore.plugin.Ingredient.CircuitIngredient;
import io.penguin.penguincore.plugin.Plugin;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

public class CircuitPlugin<V> extends Plugin<V> {

    private final CircuitBreakerOperator<V> circuitBreakerOperator;

    public CircuitPlugin(Mono<V> source, AllIngredient allIngredient) {
        super(source, allIngredient);
        circuitBreakerOperator = (CircuitBreakerOperator<V>) allIngredient.getCircuitIngredient().getCircuitBreakerOperator();
    }

    @Override
    public Publisher<V> apply(Publisher<V> before) {
        return circuitBreakerOperator.apply(before);
    }
}
