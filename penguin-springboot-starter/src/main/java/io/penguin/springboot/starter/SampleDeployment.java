package io.penguin.springboot.starter;

import io.penguin.pengiunlettuce.LettuceCache;
import io.penguin.penguincore.reader.Reader;
import reactor.core.publisher.Mono;

public class SampleDeployment<K, V> {

    private final LettuceCache<K, V> redisCache;
    private final Reader<K, V> source;

    public SampleDeployment(LettuceCache<K, V> redisCache, Reader<K, V> source) {
        this.redisCache = redisCache;
        this.source = source;
    }

    public Mono<V> findOne(K key) {
        return redisCache.findOne(key)
                .switchIfEmpty(Mono.create(i -> {
                    source.findOne(key)
                            .doOnNext(j -> redisCache.insertQueue(key))
                            .doOnNext(i::success)
                            .subscribe();

                }));

    }
}
