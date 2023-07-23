package io.penguin.penguincore.util;

import io.penguin.penguincore.plugin.bulkhead.BulkHeadOperator;
import io.penguin.penguincore.plugin.bulkhead.BulkheadGenerator;
import io.penguin.penguincore.plugin.bulkhead.BulkheadModel;
import io.penguin.penguincore.plugin.circuit.CircuitGenerator;
import io.penguin.penguincore.plugin.circuit.CircuitModel;
import io.penguin.penguincore.plugin.circuit.CircuitOperator;
import io.penguin.penguincore.plugin.timeout.TimeoutGenerator;
import io.penguin.penguincore.plugin.timeout.TimeoutModel;
import io.penguin.penguincore.plugin.timeout.TimeoutOperator;
import io.penguin.penguincore.reader.Reader;
import reactor.core.publisher.Mono;

import java.util.function.BiFunction;
import java.util.function.Function;

public class ReaderDelegate<K, V> implements Reader<K, V> {

    private final BiFunction<K, Throwable, Mono<V>> fallback;
    private final Class<V> clazz;
    private Function<K, Mono<V>> caller;

    public ReaderDelegate(Function<K, Mono<V>> caller, BiFunction<K, Throwable, Mono<V>> fallback, Class<V> clazz) {
        this.fallback = fallback;
        this.caller = caller;
        this.clazz = clazz;
    }

    public void addBulkHead(BulkheadModel bulkheadModel) {
        caller = i -> new BulkHeadOperator<>(caller.apply(i), new BulkheadGenerator<V>(bulkheadModel).generate(clazz));
    }

    public void addTimeout(TimeoutModel timeoutModel) {
        caller = i -> new TimeoutOperator<>(caller.apply(i), new TimeoutGenerator(timeoutModel).generate(clazz));
    }

    public void addCircuit(CircuitModel circuitModel) {
        caller = i -> new CircuitOperator<>(caller.apply(i), new CircuitGenerator<V>(circuitModel).generate(clazz));
    }

    @Override
    public Mono<V> findOne(K key) {
        return this.caller.apply(key);
    }

    @Override
    public Mono<V> failFindOne(K key, Throwable error) {
        return this.fallback.apply(key, error);
    }
}
