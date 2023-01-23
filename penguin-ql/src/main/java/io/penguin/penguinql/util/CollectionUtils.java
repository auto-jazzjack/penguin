package io.penguin.penguinql.util;

import java.util.Collection;
import java.util.Map;

public class CollectionUtils {
    public static <E> boolean isEmpty(Collection<E> c) {
        return c == null || c.isEmpty();
    }

    public static <K, V> boolean isEmpty(Map<K, V> c) {
        return c == null || c.isEmpty();
    }
}
