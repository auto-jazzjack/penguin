package io.penguin.penguincore.util;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Timer;
import io.penguin.penguincore.reader.Reader;
import reactor.core.publisher.Mono;
import reactor.util.context.ContextView;

import java.time.Duration;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * This Reader provide metric.
 */
public class MetricReader<K, V> implements Reader<K, V> {

    private final Reader<K, V> reader;

    final Counter successCount;
    final Timer timer;

    private static final String PENGUIN_READER_START = "PENGUIN_READER_START";

    public MetricReader(Reader<K, V> reader) {
        this.reader = reader;
        this.successCount = Counter.builder(reader.getClass().getSimpleName() + "_success")
                .tag("output", "success")
                .register(Metrics.globalRegistry);
        this.timer = Timer.builder(reader.getClass().getSimpleName() + "_latency")
                .publishPercentiles(0.5, 0.90, 0.95, 0.99)
                .register(Metrics.globalRegistry);
    }


    @Override
    public Mono<V> findOne(K key) {
        return Mono.deferContextual(new Function<ContextView, Mono<V>>() {
                    @Override
                    public Mono<V> apply(ContextView contextView) {
                        return reader.findOne(key)
                                .doOnSuccess(new Consumer<V>() {
                                    @Override
                                    public void accept(V v) {
                                        System.out.println(System.currentTimeMillis());
                                        Long start = contextView.getOrDefault(PENGUIN_READER_START, -1L);
                                        if (start != null && start > 0) {
                                            timer.record(Duration.ofMillis(System.currentTimeMillis() - start));
                                        }
                                        successCount.increment();
                                    }
                                });
                    }
                })
                .contextWrite(ctx -> {
                    System.out.println(System.currentTimeMillis());
                    return ctx.put(PENGUIN_READER_START, System.currentTimeMillis());
                });

    }

    @Override
    public Mono<V> failFindOne(K key, Throwable error) {
        return null;

    }
}
