package io.penguin.penguincore.reader;

import reactor.core.publisher.Mono;

public interface Reader<K, V> {
    Mono<V> findOne(K key);
}
