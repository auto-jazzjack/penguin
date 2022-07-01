package com.example.penguinql.core;

import reactor.core.publisher.Mono;

public interface RootResolver<Myself> extends Resolver<Void, Myself> {
    @Override
    default Mono<Myself> generate(DataFetchingEnv condition) {
        return Mono.empty();
    }

    @Override
    default void setData(Void unused, Myself data) {

    }

    @Override
    default void preHandler(ContextQL context) {
        Resolver.super.preHandler(context);
    }
}
