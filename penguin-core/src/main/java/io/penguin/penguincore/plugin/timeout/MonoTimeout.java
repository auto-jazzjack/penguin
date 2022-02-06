package io.penguin.penguincore.plugin.timeout;

import io.penguin.penguincore.plugin.Ingredient.TimeoutIngredient;
import lombok.extern.slf4j.Slf4j;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoOperator;

@Slf4j
public class MonoTimeout<V> extends MonoOperator<V, V> {

    private final TimeoutIngredient timeout;

    public MonoTimeout(Mono<? extends V> source, TimeoutIngredient timeoutIngredient) {
        super(source);
        this.timeout = timeoutIngredient;
    }

    @Override
    public void subscribe(CoreSubscriber<? super V> actual) {
        source.subscribe(new TimeoutSubscriber<>(actual, timeout.getFail(), timeout.getTimer(), timeout.getMilliseconds()));
    }
}