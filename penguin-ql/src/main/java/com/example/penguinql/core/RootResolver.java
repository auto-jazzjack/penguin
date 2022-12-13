package com.example.penguinql.core;

public interface RootResolver<Myself> extends Resolver<Void, Myself> {

    @Override
    default void setData(Void unused, Myself data) {
        //do nothing
    }

    Class<Myself> clazz();
}
