package io.penguin.springboot.starter.kind;

import io.penguin.penguincore.reader.BaseCacheReader;
import io.penguin.penguincore.reader.Reader;
import io.penguin.springboot.starter.Deployment;
import io.penguin.springboot.starter.config.Penguin;
import io.penguin.springboot.starter.model.ReaderBundle;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
public class BaseDeployment<K, V> implements Deployment<K, V> {

    private Reader<K, V> source;
    private BaseCacheReader<K, V> remoteCache;


    public BaseDeployment(Penguin penguin, Map<String, ReaderBundle> readerBundleMap) {
        for (Penguin.Container i : penguin.getSpec().getContainers()) {
            ReaderBundle readerBundle = readerBundleMap.get(i.getName());

            switch (readerBundle.getKind()) {
                case REMOTE_CACHE:
                    this.remoteCache = (BaseCacheReader<K, V>) readerBundle.getReader();
                case SOURCE:
                    this.source = readerBundle.getReader();
                default:
                    log.warn("No such container");
            }
        }

    }


    public Mono<V> findOne(K key) {
        return remoteCache.findOne(key)
                .switchIfEmpty(Mono.create(i -> {
                    source.findOne(key)
                            .doOnNext(j -> remoteCache.insertQueue(key))
                            .doOnNext(i::success)
                            .subscribe();
                }));

    }
}
