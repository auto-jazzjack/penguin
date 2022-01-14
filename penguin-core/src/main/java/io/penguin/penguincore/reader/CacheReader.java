package io.penguin.penguincore.reader;

import io.penguin.penguincore.writer.Writer;
import reactor.core.publisher.Mono;

public interface CacheReader<K, V> extends Reader<K, V>, Writer<K, V> {
    Mono<V> fromDownStream(K key);
}
