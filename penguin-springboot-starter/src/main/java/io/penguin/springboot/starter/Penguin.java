package io.penguin.springboot.starter;

import io.penguin.springboot.starter.flow.From;
import org.springframework.data.repository.Repository;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface Penguin<K, V> extends Repository<K, V> {
    Mono<V> findOne(K key);
    Mono<Map<From, V>> debugOne(K key);
    String getName();
    void refreshOne(K key);
}
