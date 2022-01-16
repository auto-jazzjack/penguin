package io.penguin.penguincore.plugin.timeout;

import io.netty.util.HashedWheelTimer;
import io.penguin.penguincore.plugin.Ingredient.AllIngredient;
import io.penguin.penguincore.plugin.Ingredient.TimeoutIngredient;
import io.penguin.penguincore.plugin.Plugin;
import org.reactivestreams.Publisher;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.Mono;

public class TimeoutPlugin<V> extends Plugin<V> {

    private final HashedWheelTimer timer;
    private final long milliseconds;

    public TimeoutPlugin(Mono<V> source, AllIngredient ingredient) {
        super(source, ingredient);
        milliseconds = ingredient.getTimeoutIngredient().getMilliseconds();
        timer = ingredient.getTimeoutIngredient().getTimer();
    }


    @Override
    public void subscribe(CoreSubscriber<? super V> actual) {
        source.subscribe(new Timer<>(actual, timer, milliseconds));
    }

    @Override
    public Publisher<V> apply(Publisher<V> before) {
        source = (Mono<V>) before;
        return this;
    }
}
