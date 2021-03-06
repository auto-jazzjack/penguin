package com.example.penguinql.core;


import com.example.penguinql.core.setter.GeneralFieldMethod;
import com.example.penguinql.core.setter.GenericType;
import com.example.penguinql.core.setter.POJOFieldMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.penguinql.core.setter.FieldUtils.PRIMITIVES;


@Slf4j
public class PojoFieldCleanser<T> {

    private final FieldCleanerMeta<T> fieldCleanerMeta;

    private static final String VALUE = "value";

    public PojoFieldCleanser(Class<T> root) throws Exception {
        fieldCleanerMeta = new FieldCleanerMeta<>(root, GenericType.NONE);
        fieldCleanerMeta.setMethod(new POJOFieldMethod<>((Constructor<Object>) root.getConstructor(), GenericType.NONE));
    }

    public T exec(T result, ExecutionPlan executionPlan) {
        try {
            return exec0(result, this.fieldCleanerMeta, executionPlan);
        } catch (Exception e) {
            log.error("");
            throw new RuntimeException(e);
        }
    }

    private <T1> T1 exec0(T1 result, FieldCleanerMeta<T1> fieldMeta, ExecutionPlan executionPlan) {
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
                .filter(i -> fieldMeta.getExtendableChildren().containsKey(i))
                .forEach(i -> {
                    FieldCleanerMeta<T1> nextFieldMeta = (FieldCleanerMeta<T1>) fieldMeta.getExtendableChildren().get(i);
                    ExecutionPlan next = executionPlan.getNext().get(i);
                    switch (nextFieldMeta.getGenericType()) {
                        case MAP:
                        case NONE:
                            T1 res = exec0((T1) nextFieldMeta.getMethod().getData(result), nextFieldMeta, next);
                            nextFieldMeta.getMethod().setData(retv, res);
                            break;
                        case LIST:
                            FieldCleanerMeta<T1> listValue = (FieldCleanerMeta<T1>) nextFieldMeta.getExtendableChildren().get(VALUE);

                            List<Object> collect = ((List<Object>) nextFieldMeta.getMethod().getData(result))
                                    .stream()
                                    .map(j -> {
                                        return exec0((T1) j, listValue, next);
                                    })
                                    .collect(Collectors.toList());
                            nextFieldMeta.getMethod().setData(retv, collect);
                            break;
                        case SET:
                        default:
                            throw new RuntimeException("Should not be reached");
                    }


                });

        return retv;
    }


    @Data
    @AllArgsConstructor
    @Builder
    static class FieldCleanerMeta<P> {
        private GenericType genericType;
        private GeneralFieldMethod<P, Object> method;

        private Map<String, FieldCleanerMeta<?>> leafChildren;
        private Map<String, FieldCleanerMeta<?>> extendableChildren;


        public FieldCleanerMeta(Class<P> root, GenericType genericType) throws Exception {

            this.leafChildren = new HashMap<>();
            this.genericType = genericType;
            this.extendableChildren = new HashMap<>();

            switch (genericType) {
                case LIST:
                case SET:
                    if (root.isAssignableFrom(List.class)) {
                        FieldCleanerMeta<P> cleanerMeta = new FieldCleanerMeta<>(root, GenericType.LIST);
                        cleanerMeta.setMethod(new POJOFieldMethod<>((Constructor<Object>) root.getConstructor(), GenericType.LIST));
                        this.extendableChildren.put(VALUE, cleanerMeta);
                    } else {
                        FieldCleanerMeta<P> cleanerMeta = new FieldCleanerMeta<>(root, GenericType.NONE);
                        cleanerMeta.setMethod(new POJOFieldMethod<>((Constructor<Object>) root.getConstructor(), GenericType.NONE));
                        this.extendableChildren.put(VALUE, cleanerMeta);
                    }

                    break;
                case NONE:
                default:
                    List<Field> allFields = Arrays.stream(root.getDeclaredFields()).collect(Collectors.toList());

                    Set<Field> leaf = allFields.stream().filter(i -> PRIMITIVES.contains(i.getType())).collect(Collectors.toSet());
                    Set<Field> nonLeaf = allFields.stream().filter(i -> !PRIMITIVES.contains(i.getType())).collect(Collectors.toSet());
                    for (Field i : leaf) {
                        this.leafChildren.put(i.getName(), FieldCleanerMeta.builder()
                                .method(new POJOFieldMethod<>((Class<Object>) root, i))
                                .genericType(GenericType.NONE)
                                .build());
                    }

                    for (Field i : nonLeaf) {

                        if (i.getType().isAssignableFrom(List.class) || i.getType().isAssignableFrom(Set.class)) {
                            Type actualTypeArgument = ((ParameterizedType) i.getGenericType()).getActualTypeArguments()[0];
                            GenericType childGeneric = i.getType().isAssignableFrom(List.class) ? GenericType.LIST : GenericType.SET;
                            FieldCleanerMeta<?> build = new FieldCleanerMeta<>((Class<? extends Object>) actualTypeArgument, childGeneric);
                            build.setMethod(new POJOFieldMethod(root, i));
                            this.extendableChildren.put(i.getName(), build);
                        } else if (i.getType().isAssignableFrom(Map.class)) {
                            Type actualTypeArgument = ((ParameterizedType) i.getGenericType()).getActualTypeArguments()[1];
                            FieldCleanerMeta<?> build = new FieldCleanerMeta<>((Class<?>) actualTypeArgument, GenericType.MAP);
                            build.setMethod(new POJOFieldMethod(root, i));
                            this.extendableChildren.put(i.getName(), build);
                        } else {
                            FieldCleanerMeta<?> build = new FieldCleanerMeta<>(i.getType(), GenericType.NONE);
                            build.setMethod(new POJOFieldMethod(root, i));
                            this.extendableChildren.put(i.getName(), build);
                        }
                    }
                    break;
            }


        }
    }
}
