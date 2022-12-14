package com.example.penguinql.core;

import lombok.Getter;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

@Getter
public class ResolverMeta<Myself> {

    private final Class<? extends Resolver<Myself>> currentClazz;

    /**
     * In case of Map, Value will be clazz
     * In case of List, Internal Type will be clazz
     * <p>
     * caution) nested type is not supported
     */
    private final Class<?> clazz;
    private Resolver<Myself> current;
    private BiConsumer<Object, Myself> setter;

    static Map<Class<?>, Map<String, Method>> cachedSetter = new ConcurrentHashMap<>();

    public ResolverMeta(Class<? extends Resolver<Myself>> currentClazz, Class<?> clazz) {
        this.currentClazz = currentClazz;
        this.clazz = clazz;
    }

    public ResolverMeta<Myself> decorateSetter(Class<?> parent, String name) {
        try {
            Map<String, Method> methods = cachedSetter.get(parent);
            if (methods == null) {
                cachedSetter.put(parent, new ConcurrentHashMap<>());
                methods = cachedSetter.get(parent);
            }
            Method cached = methods.get(name);
            if (cached == null) {
                Method cached0 = Arrays.stream(Introspector.getBeanInfo(parent).getPropertyDescriptors())
                        .filter(i -> i.getName().equalsIgnoreCase(name))
                        .map(PropertyDescriptor::getWriteMethod)
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException("Cannot create setter"));
                methods.put(name, cached0);

                setter = (o, myself) -> {
                    try {
                        cached0.invoke(o, myself);
                    } catch (Exception e) {
                        throw new IllegalStateException("Cannot set data");
                    }
                };
            } else {
                setter = (o, myself) -> {
                    try {
                        cached.invoke(o, myself);
                    } catch (Exception e) {
                        throw new IllegalStateException("Cannot set data");
                    }
                };
            }
        } catch (Exception e) {
            throw new IllegalStateException("Cannot create setter");
        }

        return this;
    }

    public ResolverMeta<Myself> decorateResolver(ResolverMapper resolverMapper) {
        current = (Resolver<Myself>) resolverMapper.toInstant(this.currentClazz);
        return this;
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
