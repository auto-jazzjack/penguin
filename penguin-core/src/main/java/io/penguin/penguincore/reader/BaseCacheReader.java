package io.penguin.penguincore.reader;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import io.penguin.penguincore.metric.MetricCreator;
import io.penguin.penguincore.util.Pair;
import io.penguin.penguincore.writer.Writer;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.time.Duration;
import java.util.Objects;

@Slf4j
public abstract class BaseCacheReader<K, V> implements Reader<K, CacheContext<V>>, Writer<K, CacheContext<V>> {

    private static final String SOURCE_CACHE_REFRESH_LATENCY = "source_refresh_cache_latency";
    private static final String SOURCE_CACHE_REFRESH_COUNT = "source_refresh_cache_count";

    private final Sinks.Many<K> watcher;
    private final Reader<K, V> fromDownStream;

    private final Timer timer = MetricCreator.timer(SOURCE_CACHE_REFRESH_LATENCY, "kind", this.getClass().getSimpleName());
    private final Counter counter = MetricCreator.counter(SOURCE_CACHE_REFRESH_COUNT, "kind", this.getClass().getSimpleName());

    public BaseCacheReader(Reader<K, V> fromDownStream) {
        Objects.requireNonNull(fromDownStream);
        watcher = Sinks.many()
                .unicast()
                .onBackpressureBuffer();

        this.fromDownStream = fromDownStream;
        /*watcher.asFlux()
                .windowTimeout(100, Duration.ofSeconds(5))
                .flatMap(Flux::distinct)
                .flatMap(i -> fromDownStream(i)
                        .onErrorResume(k -> Mono.empty())
                        .map(j -> Pair.of(i, j)))
                .filter(i -> i.getKey() != null && i.getValue() != null)
                .subscribe(i -> writeOne(i.getKey().toString(), i.getValue()), e -> log.error("", e));*/
    }


    public void insertQueue(K k) {
        watcher.emitNext(k, (signalType, emitResult) -> true);
    }

}
