package io.penguin.penguincore.reader;

import io.penguin.penguincore.writer.Writer;

public interface BaseCacheReader<K, V> extends Reader<K, CacheContext<V>>, Writer<K, CacheContext<V>> {

    default String cacheName() {
        return this.getClass().getSimpleName();
    }

    default String prefix() {
        return "";
    }
}
