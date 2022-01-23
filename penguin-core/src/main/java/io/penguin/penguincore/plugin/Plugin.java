package io.penguin.penguincore.plugin;

import io.penguin.penguincore.plugin.Ingredient.AllIngredient;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.Mono;


public abstract class Plugin<V> {

    protected Mono<V> source;

    public Plugin() {
    }

    abstract public Mono<V> decorateSource(Mono<V> source);
}
