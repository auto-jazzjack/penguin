package com.example.penguinql.core.prune;

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
}
