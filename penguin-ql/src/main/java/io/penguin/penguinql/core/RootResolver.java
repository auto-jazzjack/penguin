package io.penguin.penguinql.core;

public interface RootResolver<Myself> extends Resolver<Myself> {
    Class<Myself> clazz();
}
