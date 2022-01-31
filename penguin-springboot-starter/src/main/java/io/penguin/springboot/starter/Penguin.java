package io.penguin.springboot.starter;

import org.springframework.data.repository.Repository;
import reactor.core.publisher.Mono;

public interface Penguin<K, V> extends Repository<K, V> {
    Mono<V> findOne(K key);
}
