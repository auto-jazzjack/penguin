package io.penguin.penguincore.plugin;

import org.reactivestreams.Publisher;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoOperator;


public abstract class Plugin<V> extends MonoOperator<V, V> {

    protected final PluginInput pluginInput;
    protected Mono<V> source;

    public Plugin(PluginInput pluginInput, Mono<V> source) {
        super(source);
        this.pluginInput = pluginInput;
        this.source = source;
    }


    public int order() {
        return 0;
    }

    abstract public boolean support();

    abstract public Publisher<V> apply(Publisher<V> before);

    @Override
    public void subscribe(CoreSubscriber<? super V> actual) {
        source.subscribe(actual);
    }
}
