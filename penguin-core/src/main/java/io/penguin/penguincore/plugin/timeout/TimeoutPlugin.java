package io.penguin.penguincore.plugin.timeout;

import io.penguin.penguincore.plugin.Plugin;
import io.penguin.penguincore.plugin.TimeoutDecorator;
import reactor.core.publisher.Mono;

public class TimeoutPlugin<V> implements Plugin<V> {

    private final TimeoutDecorator timeoutDecorator;

    public TimeoutPlugin(TimeoutDecorator timeoutDecorator) {
        this.timeoutDecorator = timeoutDecorator;
    }

    @Override
    public Mono<V> decorateSource(Mono<V> source) {
        return new MonoTimeout<>(source, this.timeoutDecorator);
    }


}