package io.penguin.springboot.starter.util;

import java.util.Collection;

public class CollectionsUtils {

    public static <E> boolean isEmpty(Collection<E> collection) {
        return collection == null || collection.isEmpty();
    }

    public static <E> boolean isNotEmpty(Collection<E> collection) {
        return !isEmpty(collection);
    }

}
