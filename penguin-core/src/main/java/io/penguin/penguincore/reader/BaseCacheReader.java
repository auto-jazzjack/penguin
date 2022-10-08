package io.penguin.penguincore.reader;

import io.penguin.penguincore.writer.Writer;

public interface BaseCacheReader<K, V> extends Reader<K, CacheContext<V>>, Writer<K, CacheContext<V>> {

    String SOURCE_CACHE_REFRESH_LATENCY = "source_refresh_cache_latency";
    String SOURCE_CACHE_REFRESH_COUNT = "source_refresh_cache_count";


}
