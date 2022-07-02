package com.example.penguinql.core;


import com.example.penguinql.core.setter.GeneralFieldMethod;
import com.example.penguinql.core.setter.POJOFieldMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Input) execution plan
 * Output)
 */
public class PojoFieldCleanser<T> {

    private FieldCleanerMeta<T> fieldCleanerMeta;
    private static final Set<Class<?>> PRIMITIVES = Stream.of(
                    Short.class, short.class,
                    Integer.class, int.class,
                    long.class, Long.class,
                    Float.class, float.class,
                    Double.class, double.class,
                    String.class,
                    byte.class,
                    Character.class, char.class
            )
            .collect(Collectors.toSet());

    public PojoFieldCleanser(Class<T> root) throws Exception {
        fieldCleanerMeta = new FieldCleanerMeta<>(root);
    }

    /**
     *
     */
    public T exec(T result, ExecutionPlan executionPlan) {
        return null;
    }

    @Data
    @AllArgsConstructor
    @Builder
    static class FieldCleanerMeta<P> {
        private GeneralFieldMethod<P, Object> method;
        private Map<Class<?>, FieldCleanerMeta<?>> children;

        public FieldCleanerMeta(Class<P> root) throws Exception {
            List<Field> allFields = Arrays.stream(root.getDeclaredFields()).collect(Collectors.toList());

            Set<Field> leaf = allFields.stream().filter(i -> PRIMITIVES.contains(i.getType())).collect(Collectors.toSet());
            Set<Field> nonLeaf = allFields.stream().filter(i -> !PRIMITIVES.contains(i.getType())).collect(Collectors.toSet());

            this.children = new HashMap<>();

            for (Field i : leaf) {
                this.children.put(i.getDeclaringClass(), FieldCleanerMeta.builder()
                        .method(new POJOFieldMethod<>((Class<Object>) root, i))
                        .build());
            }

            for (Field i : nonLeaf) {
                if (i.getType().isAssignableFrom(List.class)) {
                    Type actualTypeArgument = ((ParameterizedType) i.getGenericType()).getActualTypeArguments()[0];
                    FieldCleanerMeta<?> build = new FieldCleanerMeta<>((Class<?>) actualTypeArgument);
                    this.children.put(i.getDeclaringClass(), build);
                } else if (i.getType().isAssignableFrom(Map.class)) {
                    Type actualTypeArgument = ((ParameterizedType) i.getGenericType()).getActualTypeArguments()[1];
                    FieldCleanerMeta<?> build = new FieldCleanerMeta<>((Class<?>) actualTypeArgument);
                    this.children.put(i.getDeclaringClass(), build);
                } else {
                    FieldCleanerMeta<?> build = new FieldCleanerMeta<>(i.getDeclaringClass());
                    this.children.put(i.getDeclaringClass(), build);
                }
            }

        }
    }
}
