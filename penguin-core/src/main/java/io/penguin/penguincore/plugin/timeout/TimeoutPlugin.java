package io.penguin.penguincore.plugin.timeout;

import io.penguin.penguincore.plugin.Ingredient.TimeoutDecorator;
import io.penguin.penguincore.plugin.Plugin;
import reactor.core.publisher.Mono;

public class TimeoutPlugin<V> extends Plugin<V> {

    private final TimeoutDecorator timeoutIngredient;

    public TimeoutPlugin(TimeoutDecorator ingredient) {
        super();
        timeoutIngredient = ingredient;
    }

    @Override
    public Mono<V> decorateSource(Mono<V> source) {
        return new MonoTimeout<>(source, this.timeoutIngredient);
    }


}
