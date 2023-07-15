package io.penguin.penguincore.plugin.timeout;

import io.penguin.penguincore.plugin.TimeoutDecorator;
import lombok.extern.slf4j.Slf4j;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoOperator;

@Slf4j
public class MonoTimeout<V> extends Mono<V> {

    private final TimeoutDecorator timeout;

    public MonoTimeout(TimeoutDecorator timeoutDecorator) {
        this.timeout = timeoutDecorator;
    }

    @Override
    public void subscribe(CoreSubscriber<? super V> actual) {
        new TimeoutSubscriber<>(actual, timeout.getFail(), timeout.getTimer(), timeout.getMilliseconds());
    }
}