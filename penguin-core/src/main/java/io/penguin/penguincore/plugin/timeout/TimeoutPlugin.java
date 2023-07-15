package io.penguin.penguincore.plugin.timeout;

import io.penguin.penguincore.plugin.decorator.TimeoutDecorator;
import io.penguin.penguincore.plugin.Plugin;
import reactor.core.publisher.Mono;

public class TimeoutPlugin<V> extends Plugin<V> {

    private final TimeoutDecorator timeoutDecorator;

    public TimeoutPlugin(TimeoutDecorator timeoutDecorator) {
        super();
        this.timeoutDecorator = timeoutDecorator;
    }

    @Override
    public Mono<V> decorateSource(Mono<V> source) {
        return new MonoTimeout<>(source, this.timeoutDecorator);
    }


}
