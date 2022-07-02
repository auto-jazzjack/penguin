package com.example.penguinql.core;


import com.example.penguinql.core.setter.GeneralFieldMethod;
import com.example.penguinql.core.setter.GenericType;
import com.example.penguinql.core.setter.POJOFieldMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.penguinql.core.setter.FieldUtils.PRIMITIVES;


public class PojoFieldCleanser<T> {

    private final FieldCleanerMeta<T> fieldCleanerMeta;


    public PojoFieldCleanser(Class<T> root) throws Exception {
        fieldCleanerMeta = new FieldCleanerMeta<>(root);
        fieldCleanerMeta.setGenericType(GenericType.NONE);
        fieldCleanerMeta.setMethod(POJOFieldMethod.<T, Object>builder()
                .newInstance((Constructor<Object>) root.getConstructor())
                .build());
    }

    public T exec(T result, ExecutionPlan executionPlan) {
        try {
            return exec0(result, this.fieldCleanerMeta, executionPlan);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private  <T1> T1 exec0(T1 result, FieldCleanerMeta<T1> fieldMeta, ExecutionPlan executionPlan) {
        T1 retv = (T1) fieldMeta.getMethod().defaultInstance();

        Optional.ofNullable(executionPlan.getCurrFields()).orElse(Collections.emptySet())
                .stream()
                .filter(i -> fieldMeta.getLeafChildren().containsKey(i))
                .forEach(i -> {
                    FieldCleanerMeta<T1> fieldCleanerMeta = (FieldCleanerMeta<T1>) fieldMeta.getLeafChildren().get(i);
                    fieldCleanerMeta.getMethod().setData(retv, fieldCleanerMeta.getMethod().getData(result));
                });

        Optional.ofNullable(executionPlan.getCurrObjects()).orElse(Collections.emptySet())
                .stream()
                .filter(i -> fieldCleanerMeta.getExtendableChildren().containsKey(i))
                .forEach(i -> {
                    FieldCleanerMeta<T1> fieldCleanerMeta = (FieldCleanerMeta<T1>) this.fieldCleanerMeta.getExtendableChildren().get(i);
                    switch (fieldCleanerMeta.getGenericType()) {
                        case MAP:

                        case NONE:
                        case LIST:
                            //List<Object> data = (List<Object>) fieldCleanerMeta.getMethod().getData(result);

                            List<Object> collect = ((List<Object>)result)
                                    .stream()
                                    .map(j -> exec0((T1) fieldCleanerMeta.getMethod().getData((T1) j), fieldMeta, executionPlan.getNext().get(i)))
                                    .collect(Collectors.toList());
                            fieldCleanerMeta.getMethod().setData(retv, collect);
                            break;
                        case SET:
                        default:
                            throw new RuntimeException("Should not be reached");
                    }


                });

        return retv;
    }


    private Object valueExtractor(Object obj, Field field) {
        return null;
    }

    @Data
    @AllArgsConstructor
    @Builder
    static class FieldCleanerMeta<P> {
        private GenericType genericType;
        private GeneralFieldMethod<P, Object> method;

        private Map<String, FieldCleanerMeta<?>> leafChildren;
        private Map<String, FieldCleanerMeta<?>> extendableChildren;


        public FieldCleanerMeta(Class<P> root) throws Exception {
            List<Field> allFields = Arrays.stream(root.getDeclaredFields()).collect(Collectors.toList());

            Set<Field> leaf = allFields.stream().filter(i -> PRIMITIVES.contains(i.getType())).collect(Collectors.toSet());
            Set<Field> nonLeaf = allFields.stream().filter(i -> !PRIMITIVES.contains(i.getType())).collect(Collectors.toSet());

            this.leafChildren = new HashMap<>();
            this.extendableChildren = new HashMap<>();

            for (Field i : leaf) {
                this.leafChildren.put(i.getName(), FieldCleanerMeta.builder()
                        .method(new POJOFieldMethod<>((Class<Object>) root, i))
                        .genericType(GenericType.NONE)
                        .build());
            }

            for (Field i : nonLeaf) {
                if (i.getType().isAssignableFrom(List.class) || i.getType().isAssignableFrom(Set.class)) {
                    Type actualTypeArgument = ((ParameterizedType) i.getGenericType()).getActualTypeArguments()[0];
                    FieldCleanerMeta<?> build = new FieldCleanerMeta<>((Class<?>) actualTypeArgument);
                    build.setMethod(new POJOFieldMethod(root, i));
                    build.setGenericType(i.getType().isAssignableFrom(List.class) ? GenericType.LIST : GenericType.SET);
                    this.extendableChildren.put(i.getName(), build);
                } else if (i.getType().isAssignableFrom(Map.class)) {
                    Type actualTypeArgument = ((ParameterizedType) i.getGenericType()).getActualTypeArguments()[1];
                    FieldCleanerMeta<?> build = new FieldCleanerMeta<>((Class<?>) actualTypeArgument);
                    build.setMethod(new POJOFieldMethod(root, i));
                    build.setGenericType(GenericType.MAP);
                    this.extendableChildren.put(i.getName(), build);
                } else {
                    FieldCleanerMeta<?> build = new FieldCleanerMeta<>(i.getDeclaringClass());
                    build.setMethod(new POJOFieldMethod(root, i));
                    build.setGenericType(GenericType.NONE);
                    this.extendableChildren.put(i.getName(), build);
                }
            }

        }
    }
}
