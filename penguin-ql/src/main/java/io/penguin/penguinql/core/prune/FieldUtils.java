package io.penguin.penguinql.core.prune;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FieldUtils {
    public static final Set<Class<?>> PRIMITIVES = Stream.of(
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

    public static Class<?> unWrapCollection(Field field) {
        Class<?> clazz = field.getType();

        if (clazz.isAssignableFrom(Map.class)) {
            return (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[1];
        } else if (clazz.isAssignableFrom(List.class) || clazz.isAssignableFrom(Set.class)) {
            return (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
        } else {
            return clazz;
        }
    }
}
