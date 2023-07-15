package io.penguin.penguincore.plugin;

import reactor.core.publisher.Mono;


public interface Plugin<V> {
    Mono<V> decorateSource(Mono<V> source);
}
