package io.penguin.deployment.penguindeployment;

import io.penguin.pengiuncassandra.CassandraSource;
import io.penguin.pengiunlettuce.LettuceCache;
import io.penguin.penguincore.reader.Context;
import reactor.core.publisher.Mono;

public class Deployment<K, V> {

    private final LettuceCache<K, V> redisCache;
    private final CassandraSource<K, V> cassandraSource;

    public Deployment(LettuceCache<K, V> redisCache, CassandraSource<K, V> cassandraSource) {
        this.redisCache = redisCache;
        this.cassandraSource = cassandraSource;
    }

    public Mono<V> findOne(K key) {
        return redisCache.findOne(key)
                .flatMap(i -> {
                    if (i.getValue() == null) {
                        return cassandraSource.findOne(key)
                                .doOnNext(j -> redisCache.insertQueue(key));
                    } else {
                        return Mono.just(i);
                    }
                })
                .map(Context::getValue);
    }
}
