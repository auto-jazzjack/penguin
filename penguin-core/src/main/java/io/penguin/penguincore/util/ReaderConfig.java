package io.penguin.penguincore.util;

import io.penguin.penguincore.plugin.bulkhead.BulkheadModel;
import io.penguin.penguincore.plugin.circuit.CircuitModel;
import io.penguin.penguincore.plugin.timeout.TimeoutModel;
import lombok.Builder;
import lombok.Data;
import reactor.core.publisher.Mono;

import java.util.function.BiFunction;
import java.util.function.Function;

@Data
@Builder
public class ReaderConfig<K, V> {
    private Function<K, Mono<V>> caller;
    private BiFunction<K, Throwable, Mono<V>> fallback;
    private boolean enableMetric;
    private TimeoutModel timeoutModel;
    private BulkheadModel bulkheadModel;
    private CircuitModel circuitModel;

}
