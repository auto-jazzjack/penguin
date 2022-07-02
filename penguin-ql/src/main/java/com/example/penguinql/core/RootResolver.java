package com.example.penguinql.core;

public interface RootResolver<Myself> extends Resolver<Void, Myself> {


    @Override
    default void setData(Void unused, Myself data) {

    }

    @Override
    default void preHandler(ContextQL context) {
        Resolver.super.preHandler(context);
    }
}
