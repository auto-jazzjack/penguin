package com.example.penguinql.core;

public interface RootResolver<Myself> extends Resolver<Myself> {

    Class<Myself> clazz();
}
