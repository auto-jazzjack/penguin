package io.penguin.penguincore.reader;

import io.penguin.penguincore.util.Pair;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@Slf4j
public abstract class DefaultCacheReaderImpl<K, V> implements CacheReader<K, V> {

    private final Sinks.Many<K> watcher;
    private final Reader<K, V> fromDownStream;
    private final Reader<K, V> fromThis;

    public DefaultCacheReaderImpl(Reader<K, V> fromDownStream, Reader<K, V> fromThis) {
        watcher = Sinks.many()
                .unicast()
                .onBackpressureBuffer();

        watcher.asFlux()
                .flatMap(i -> fromDownStream.findOne(i).map(j -> Pair.of(i, j)))
                .filter(i -> i.getKey() != null && i.getValue() != null)
                .subscribe(i -> writeOne(i.getKey(), i.getValue(), expireTime()), e -> log.error("", e));

        this.fromDownStream = fromDownStream;
        this.fromThis = fromThis;
    }

    @Override
    abstract public Mono<Boolean> writeOne(K key, V value, long expireTime);

    @Override
    abstract public long expireTime();

    @Override
    public Mono<V> findOne(K key) {
        return fromThis.findOne(key);
    }

    @Override
    public Mono<V> fromDownStream(K key) {
        return fromDownStream.findOne(key);
    }
}
