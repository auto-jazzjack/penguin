package com.example.penguinql.core;

import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Map;

public interface Resolver<Myself> {

    Mono<Myself> generate(DataFetchingEnv condition);

    default Map<String, ResolverMeta<?>> next() {
        return Collections.emptyMap();
    }

    default void preHandler(ContextQL context) {
        //do nothing
    }


}
