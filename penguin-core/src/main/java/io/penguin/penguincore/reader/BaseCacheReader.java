package io.penguin.penguincore.reader;

import io.penguin.penguincore.writer.Writer;

public interface BaseCacheReader<K, V> extends Reader<K, CacheContext<V>>, Writer<K, CacheContext<V>> {

}
