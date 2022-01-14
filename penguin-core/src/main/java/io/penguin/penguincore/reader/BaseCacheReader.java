package io.penguin.penguincore.reader;

import io.penguin.penguincore.util.Pair;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@Slf4j
public abstract class BaseCacheReader<K, V> implements CacheReader<K, V> {

    private final Sinks.Many<K> watcher;
    private final Reader<K, V> fromDownStream;

    public BaseCacheReader(Reader<K, V> fromDownStream) {
        watcher = Sinks.many()
                .unicast()
                .onBackpressureBuffer();

        watcher.asFlux()
                .flatMap(i -> fromDownStream.findOne(i).map(j -> Pair.of(i, j)))
                .filter(i -> i.getKey() != null && i.getValue() != null)
                .subscribe(i -> writeOne(i.getKey(), i.getValue().getValue()), e -> log.error("", e));

        this.fromDownStream = fromDownStream;
    }

    @Override
    abstract public void writeOne(K key, V value);

    @Override
    abstract public long expireTime();

    @Override
    abstract public Mono<Context<V>> findOne(K key);

    public void insertQueue(K k) {
        watcher.tryEmitNext(k);
    }

    @Override
    public Mono<Context<V>> fromDownStream(K key) {
        return fromDownStream.findOne(key);
    }
}
