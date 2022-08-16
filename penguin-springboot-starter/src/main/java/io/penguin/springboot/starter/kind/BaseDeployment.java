package io.penguin.springboot.starter.kind;

import io.penguin.penguincore.reader.BaseCacheReader;
import io.penguin.penguincore.reader.BaseOverWriteReader;
import io.penguin.penguincore.reader.Context;
import io.penguin.penguincore.reader.Reader;
import io.penguin.springboot.starter.Penguin;
import io.penguin.springboot.starter.config.PenguinProperties;
import io.penguin.springboot.starter.flow.From;
import io.penguin.springboot.starter.model.MultiBaseOverWriteReaders;
import io.penguin.springboot.starter.model.ReaderBundle;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;


@Slf4j
@Getter
public class BaseDeployment<K, V> implements Penguin<K, V> {

    private Reader<K, Context<V>> source;
    private BaseCacheReader<K, V> remoteCache;

    /**
     * Each Column can be overWritten
     */
    private MultiBaseOverWriteReaders<K, V> overWriter;
    private Map<? extends Class<? extends BaseOverWriteReader<K, Object, V>>, BiConsumer<V, Object>> mergers;
    private final String name;

    public BaseDeployment(PenguinProperties.Worker worker, Map<String, ReaderBundle<K, V>> readerBundleMap, String name) {
        this.name = name;

        for (PenguinProperties.Container i : worker.getContainers()) {
            ReaderBundle<K, V> readerBundle = readerBundleMap.get(i.getName());

            switch (readerBundle.getKind()) {
                case LETTUCE_CACHE:
                    this.remoteCache = (BaseCacheReader<K, V>) readerBundle.getReader();
                    break;
                case OVER_WRITER:
                    System.out.println();
                    if (readerBundle.getReader() instanceof MultiBaseOverWriteReaders) {
                        overWriter = (MultiBaseOverWriteReaders) readerBundle.getReader();
                        mergers = (Map) overWriter.createMergers();
                    }
                    break;
                case CASSANDRA:
                case BEAN:
                    this.source = readerBundle.getReader();
                    break;
                default:
                    log.warn("No such container");
                    break;
            }
        }
    }


    @Override
    public Mono<V> findOne(K key) {

        Mono<Context<V>> withoutOverWrite = Mono.defer(() -> remoteCache.findOne(key))
                .flatMap(i -> {
                    if (i.getValue() == null) {
                        remoteCache.insertQueue(key);
                        return source.findOne(key);
                    }
                    return Mono.just(i);
                });

        if (overWriter != null) {
            Mono<Map<Class<? extends BaseOverWriteReader<K, Object, V>>, Object>> overWriterOne = overWriter.findOne(key)
                    .defaultIfEmpty(Collections.emptyMap());

            return Mono.zip(withoutOverWrite, overWriterOne)
                    .flatMap(i -> {
                        Context<V> origin = i.getT1();
                        if (origin.getValue() == null) {
                            return Mono.empty();
                        } else {
                            Map<Class<? extends BaseOverWriteReader<K, Object, V>>, Object> t2 = i.getT2();
                            t2.forEach((key1, value) -> mergers.get(key1).accept(origin.getValue(), value));
                            return Mono.just(origin.getValue());
                        }
                    });
        } else {
            return withoutOverWrite.map(Context::getValue);
        }
    }

    @Override
    public void refreshOne(K key) {

    }

    @Override
    public Mono<Map<From, V>> debugOne(K key) {

        return Mono.zip(
                source.findOne(key),
                remoteCache.findOne(key),
                (s, r) -> {
                    Map<From, V> retv = new HashMap<>();
                    retv.put(From.SOURCE, s.getValue());
                    retv.put(From.REMOTE_CACHE, r.getValue());
                    return retv;
                }
        );

    }

}
