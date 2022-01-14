package io.penguin.deployment.penguindeployment;

import io.penguin.pengiuncassandra.CassandraSource;
import io.penguin.pengiunlettuce.LettuceCache;
import io.penguin.penguincore.reader.Context;
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
                .flatMap(i -> {
                    if (i.getValue() == null) {
                        return source.findOne(key)
                                .doOnNext(j -> redisCache.insertQueue(key));
                    } else {
                        return Mono.just(i);
                    }
                })
                .map(Context::getValue);
    }
}
