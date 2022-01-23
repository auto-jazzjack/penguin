package io.penguin.penguincore.plugin.timeout;

import io.micrometer.core.instrument.Counter;
import io.penguin.penguincore.exception.TimeoutException;
import io.penguin.penguincore.plugin.Ingredient.TimeoutIngredient;
import io.penguin.penguincore.plugin.Plugin;
import reactor.core.publisher.Mono;

public class TimeoutPlugin<V> extends Plugin<V> {

    private final TimeoutIngredient timeoutIngredient;
    private final Counter timeout;

    public TimeoutPlugin(TimeoutIngredient ingredient) {
        super();
        timeoutIngredient = ingredient;
        this.timeout = timeoutIngredient.getCounter();
    }

    @Override
    public Mono<V> decorateSource(Mono<V> source) {
        return new MonoTimeout<>(source, this.timeoutIngredient)
                .doOnError(i -> {
                    //if (i instanceof TimeoutException) {
                    //    this.timeout.increment();
                   // }
                });
    }


}
