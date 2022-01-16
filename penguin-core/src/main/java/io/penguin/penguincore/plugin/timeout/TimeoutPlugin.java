package io.penguin.penguincore.plugin.timeout;

import io.netty.util.HashedWheelTimer;
import io.penguin.penguincore.plugin.Plugin;
import io.penguin.penguincore.plugin.PluginInput;
import org.reactivestreams.Publisher;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.Optional;

public class TimeoutPlugin<V> extends Plugin<V> {

    private final HashedWheelTimer timer;
    private final long milliseconds;

    public TimeoutPlugin(PluginInput pluginInput, Mono<V> source) {
        super(pluginInput, source);
        Objects.requireNonNull(pluginInput);
        Objects.requireNonNull(pluginInput.getTimeout());

        timer = new HashedWheelTimer();
        milliseconds = pluginInput.getTimeout().getTimeoutMilliseconds();
    }

    @Override
    public int order() {
        return super.pluginInput.getTimeout().getOrder();
    }

    @Override
    public boolean support() {

        boolean empty = Optional.ofNullable(pluginInput)
                .map(PluginInput::getTimeout)
                .isEmpty();
        return !empty;
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
