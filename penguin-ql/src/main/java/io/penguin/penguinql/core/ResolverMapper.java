package io.penguin.penguinql.core;

public interface ResolverMapper {
    Resolver<Object> toInstant(Class<? extends Resolver> resolver);
}
