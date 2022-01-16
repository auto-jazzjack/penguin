package io.penguin.penguincore.plugin;

import io.penguin.penguincore.plugin.Ingredient.AllIngredient;
import org.reactivestreams.Publisher;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoOperator;


public abstract class Plugin<V> extends MonoOperator<V, V> {

    protected Mono<V> source;

    public Plugin(Mono<V> source, AllIngredient allIngredient) {
        super(source);
        this.source = source;
    }


    abstract public Publisher<V> apply(Publisher<V> before);

    @Override
    public void subscribe(CoreSubscriber<? super V> actual) {
        source.subscribe(actual);
    }
}