package io.penguin.springboot.starter.kind;

import io.penguin.penguincore.reader.BaseCacheReader;
import io.penguin.penguincore.reader.Reader;
import io.penguin.springboot.starter.Penguin;
import io.penguin.springboot.starter.config.PenguinProperties;
import io.penguin.springboot.starter.model.ReaderBundle;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
public class BaseDeployment<K, V> implements Penguin<K, V> {

    private Reader<K, V> source;
    private BaseCacheReader<K, V> remoteCache;


    public BaseDeployment(PenguinProperties.Worker worker, Map<String, ReaderBundle> readerBundleMap) {
        for (PenguinProperties.Container i : worker.getContainers()) {
            ReaderBundle readerBundle = readerBundleMap.get(i.getName());

            switch (readerBundle.getKind()) {
                case REMOTE_CACHE:
                    this.remoteCache = (BaseCacheReader<K, V>) readerBundle.getReader();
                    break;
                case SOURCE:
                case HELLO:
                case HELLO2:
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
        return remoteCache.findOne(key)
                .switchIfEmpty(Mono.create(i -> {
                    source.findOne(key)
                            .doOnNext(j -> remoteCache.insertQueue(key))
                            .doOnNext(i::success)
                            .subscribe();
                }));

    }
}
