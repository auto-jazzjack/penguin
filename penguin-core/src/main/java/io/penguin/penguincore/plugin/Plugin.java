package io.penguin.penguincore.plugin;

import reactor.core.publisher.Mono;


public abstract class Plugin<V> {

    protected Mono<V> source;

    public Plugin() {
    }

    abstract public Mono<V> decorateSource(Mono<V> source);
}
