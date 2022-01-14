package io.penguin.penguincore.reader;

import io.penguin.penguincore.util.Pair;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;
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
                .subscribe(i -> writeOne(i.getKey(), i.getValue()), e -> log.error("", e));

        this.fromDownStream = fromDownStream;
    }

    @Override
    abstract public void writeOne(K key, V value);

    @Override
    abstract public long expireSecond();

    @Override
    abstract public Mono<V> findOne(K key);

    public void insertQueue(K k) {
        watcher.emitNext(k, new Sinks.EmitFailureHandler() {
            @Override
            public boolean onEmitFailure(SignalType signalType, Sinks.EmitResult emitResult) {
                return false;
            }
        });
    }

    @Override
    public Mono<V> fromDownStream(K key) {
        return fromDownStream.findOne(key);
    }
}
