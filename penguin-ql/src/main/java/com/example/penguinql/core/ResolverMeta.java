package com.example.penguinql.core;

import lombok.Getter;

import java.util.Objects;
import java.util.function.BiConsumer;

@Getter
public class ResolverMeta<Myself> {

    private final Class<? extends Resolver<Myself>> currentClazz;
    private final Class<?> clazz;

    private Resolver<Myself> current;
    private BiConsumer<Object, Myself> setter;

    public ResolverMeta(Class<? extends Resolver<Myself>> currentClazz, Class<?> clazz) {
        this.currentClazz = currentClazz;
        this.clazz = clazz;
    }

    public ResolverMeta<Myself> decorateResolver(ResolverMapper resolverMapper) {
        current = (Resolver<Myself>) resolverMapper.toInstant(this.currentClazz);
        return this;
    }

    public void setSetter(BiConsumer<Object, Myself> setter) {
        this.setter = setter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResolverMeta<?> that = (ResolverMeta<?>) o;
        return Objects.equals(current, that.current);
    }

    @Override
    public int hashCode() {
        return Objects.hash(current);
    }
}
