package io.penguin.penguincore.plugin.timeout;

import io.penguin.penguincore.plugin.Ingredient.TimeoutIngredient;
import io.penguin.penguincore.plugin.Plugin;
import reactor.core.publisher.Mono;

public class TimeoutPlugin<V> extends Plugin<V> {

    private final TimeoutIngredient timeoutIngredient;

    public TimeoutPlugin(TimeoutIngredient ingredient) {
        super();
        timeoutIngredient = ingredient;
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
