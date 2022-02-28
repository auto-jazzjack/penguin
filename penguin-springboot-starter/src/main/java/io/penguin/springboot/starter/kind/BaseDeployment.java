package io.penguin.springboot.starter.kind;

import io.penguin.penguincore.reader.BaseCacheReader;
import io.penguin.penguincore.reader.Context;
import io.penguin.penguincore.reader.Reader;
import io.penguin.springboot.starter.Penguin;
import io.penguin.springboot.starter.config.PenguinProperties;
import io.penguin.springboot.starter.flow.From;
import io.penguin.springboot.starter.model.ReaderBundle;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;


@Slf4j
@Getter
public class BaseDeployment<K, V> implements Penguin<K, V> {

    private Reader<K, Context<V>> source;
    private BaseCacheReader<K, Context<V>> remoteCache;
    private final String name;

    public BaseDeployment(PenguinProperties.Worker worker, Map<String, ReaderBundle<K, V>> readerBundleMap, String name) {
        this.name = name;
        for (PenguinProperties.Container i : worker.getContainers()) {
            ReaderBundle<K, V> readerBundle = readerBundleMap.get(i.getName());

            switch (readerBundle.getKind()) {
                case LETTUCE_CACHE:
                    this.remoteCache = (BaseCacheReader<K, Context<V>>) readerBundle.getReader();
                    break;
                case CASSANDRA:
                case HELLO:
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

        Mono<V> map = Mono.from(remoteCache.findOne(key))
                .flatMap(i -> {
                    if (i.getValue() == null) {
                        remoteCache.insertQueue(key);
                        return source.findOne(key);
                    }
                    return Mono.just(i);
                })
                .map(Context::getValue);

        return map;
    }

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
