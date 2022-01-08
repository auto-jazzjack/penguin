package io.penguin.penguincore.writer;

import reactor.core.publisher.Mono;

public interface Writer<K, V> {
    Mono<Boolean> writeOne(K key, V value, long expireTime);
    long expireTime();
}
