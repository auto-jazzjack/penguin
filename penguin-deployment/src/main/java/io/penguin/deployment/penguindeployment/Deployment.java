package io.penguin.deployment.penguindeployment;

import io.penguin.pengiunlettuce.LettuceCache;
import io.penguin.penguincore.reader.Reader;
import reactor.core.publisher.Mono;

public class Deployment<K, V> {

    private final LettuceCache<K, V> redisCache;
    private final Reader<K, V> source;

    public Deployment(LettuceCache<K, V> redisCache, Reader<K, V> source) {
        this.redisCache = redisCache;
        this.source = source;
    }

    public Mono<V> findOne(K key) {
        return redisCache.findOne(key)
                .switchIfEmpty(Mono.create(i -> {
                    source.findOne(key)
                            .doOnNext(j -> {
                                redisCache.insertQueue(key);
                            })
                            .doOnNext(j->{
                                i.success(j);
                            }).subscribe();

                }));
    }
}
