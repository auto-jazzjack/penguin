package io.penguin.deployment.penguindeployment;

import io.penguin.pengiunlettuce.LettuceCacheWithPlugin;
import io.penguin.penguincore.reader.Reader;
import reactor.core.publisher.Mono;

public class Deployment<K, V> {

    private final LettuceCacheWithPlugin<K, V> redisCache;
    private final Reader<K, V> source;

    public Deployment(LettuceCacheWithPlugin<K, V> redisCache, Reader<K, V> source) {
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
                            .doOnNext(j -> {
                                i.success(j);
                            }).subscribe();

                }));

    }
}
