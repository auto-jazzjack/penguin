package io.penguin.springboot.starter.model;

import io.penguin.penguincore.reader.Reader;
import io.penguin.penguincore.util.Pair;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.stream.Collectors;

public class MultiReader<K> implements Reader<K, Map<String, Object>> {

    private final Map<String, Reader<K, ?>> readers;

    public MultiReader(Map<String, Reader<K, ?>> readers) {
        this.readers = readers;
    }

    @Override
    public Mono<Map<String, Object>> findOne(K key) {

        Mono<Map<String, Object>> collect = Flux.fromIterable(readers.entrySet())
                .flatMap(i -> i.getValue().findOne(key).map(j -> Pair.of(i.getKey(), j)))
                .collect(Collectors.toMap(Pair::getKey, Pair::getValue));
        return collect;
    }
}
