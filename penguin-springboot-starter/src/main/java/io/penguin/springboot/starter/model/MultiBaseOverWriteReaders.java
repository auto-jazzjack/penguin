package io.penguin.springboot.starter.model;

import io.penguin.penguincore.reader.BaseOverWriteReader;
import io.penguin.penguincore.reader.Reader;
import io.penguin.penguincore.util.Pair;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class MultiBaseOverWriteReaders<K> implements Reader<K, Map<Class<? extends BaseOverWriteReader>, Object>> {

    private final Map<Class<? extends BaseOverWriteReader>, Reader<K, ?>> readers;

    public MultiBaseOverWriteReaders(Map<Class<? extends BaseOverWriteReader>, Reader<K, ?>> readers) {
        this.readers = readers;
    }

    public Map<Class<? extends BaseOverWriteReader>, BiConsumer<Object, Object>> createMergers() {

        return this.readers.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, i -> {
                    BaseOverWriteReader<K, Object, Object> value = (BaseOverWriteReader) i.getValue();
                    return value::merge;
                }));
    }

    @Override
    public Mono<Map<Class<? extends BaseOverWriteReader>, Object>> findOne(K key) {

        return Flux.fromIterable(readers.entrySet())
                .flatMap(i -> i.getValue().findOne(key).map(j -> Pair.of(i.getKey(), j)))
                .collect(Collectors.toMap(Pair::getKey, Pair::getValue));
    }
}
