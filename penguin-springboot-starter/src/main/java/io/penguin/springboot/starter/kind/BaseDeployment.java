package io.penguin.springboot.starter.kind;

import io.penguin.penguincore.reader.StatefulCache;
import io.penguin.penguincore.reader.BaseOverWriteReader;
import io.penguin.penguincore.reader.CacheContext;
import io.penguin.penguincore.reader.Reader;
import io.penguin.penguincore.util.Pair;
import io.penguin.springboot.starter.Penguin;
import io.penguin.springboot.starter.config.PenguinProperties;
import io.penguin.springboot.starter.model.MultiBaseOverWriteReaders;
import io.penguin.springboot.starter.model.ReaderBundle;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;


@Slf4j
@Getter
public class BaseDeployment<K, V> implements Penguin<K, V> {

    private Reader<K, V> source;
    private final List<StatefulCache<K, V>> caches;


    /**
     * Each Column can be overWritten
     */
    private MultiBaseOverWriteReaders<K, V> overWriter;
    private Map<? extends Class<? extends BaseOverWriteReader<K, Object, V>>, BiConsumer<V, Object>> mergers;
    private final String name;

    public BaseDeployment(PenguinProperties.Worker worker, Map<String, ReaderBundle<K, V>> readerBundleMap, String name) {
        this.name = name;
        this.caches = new ArrayList<>();

        for (PenguinProperties.Container i : worker.getContainers()) {
            ReaderBundle<K, V> readerBundle = readerBundleMap.get(i.getName());

            switch (readerBundle.getKind()) {
                case LETTUCE_CACHE:
                    caches.add((StatefulCache<K, V>) readerBundle.getReader());
                    break;
                case OVER_WRITER:
                    System.out.println();
                    if (readerBundle.getReader() instanceof MultiBaseOverWriteReaders) {
                        overWriter = (MultiBaseOverWriteReaders) readerBundle.getReader();
                        mergers = overWriter.createMergers();
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

        Mono<CacheContext<V>> cacheChain = Mono.empty();
        AtomicInteger lastIdxCache = new AtomicInteger(-1);
        for (int i = 0; i < caches.size(); i++) {
            final int idx = i;
            cacheChain = cacheChain
                    .switchIfEmpty(Mono.defer(() -> {
                        lastIdxCache.set(idx);
                        return caches.get(idx).findOne(key);
                    }));

        }


        cacheChain = cacheChain
                .doOnNext(i -> {
                    int i1 = lastIdxCache.get();
                    //cache will be backfilled by right before one
                    if (i1 - 1 >= 0) {
                        caches.get(lastIdxCache.get()).writeOne(key, i);
                    }
                })
                //if result still empty, we need to use source
                .switchIfEmpty(source.findOne(key)
                        .map(i -> Pair.of(i, System.currentTimeMillis()))
                        .map(i -> new CacheContext<V>() {
                            @Override
                            public V getValue() {
                                return i.getKey();
                            }

                            @Override
                            public long getTimeStamp() {
                                return i.getValue();
                            }
                        })
                        .doOnNext(i -> {
                            if (caches.size() > 0) {
                                caches.get(0).writeOne(key, i);
                            }
                        })
                );


        if (overWriter != null) {
            Mono<Map<Class<? extends BaseOverWriteReader<K, Object, V>>, Object>> overWriterOne = overWriter.findOne(key)
                    .defaultIfEmpty(Collections.emptyMap());

            return Mono.zip(cacheChain, overWriterOne)
                    .flatMap(i -> {
                        CacheContext<V> origin = i.getT1();
                        Map<Class<? extends BaseOverWriteReader<K, Object, V>>, Object> t2 = i.getT2();
                        t2.forEach((key1, value) -> mergers.get(key1).accept(origin.getValue(), value));
                        return Mono.just(origin.getValue());
                    });
        } else {
            return cacheChain.map(CacheContext::getValue);
        }
    }


    @Override
    public void refreshOne(K key) {

    }

    @Override
    public Mono<Map<String, Object>> debugOne(K key) {

        Mono<Map<String, CacheContext<V>>> mapMono = Flux.fromIterable(caches)
                .flatMap(i -> Mono.defer(() -> i.findOne(key))
                        .map(j -> Pair.of(i.cacheName(), j))
                        .subscribeOn(Schedulers.boundedElastic())
                )
                .collectMap(Pair::getKey, Pair::getValue)
                .defaultIfEmpty(Collections.emptyMap());

        Mono<Map<String, Object>> source = this.source.findOne(key)
                .map(i -> {
                    Map<String, Object> retv = new HashMap<>();
                    retv.put("SOURCE", i);
                    return retv;
                })
                .defaultIfEmpty(Collections.emptyMap());

        return Mono.zip(
                mapMono,
                source,
                (s, r) -> {
                    Map<String, Object> retv = new HashMap<>();
                    retv.putAll(s);
                    retv.putAll(r);
                    return retv;
                }
        );

    }

}
