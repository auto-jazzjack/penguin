package io.penguin.penguinql.util;

import io.penguin.penguinql.core.Query;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import static io.penguin.penguinql.core.prune.FieldUtils.PRIMITIVES;
import static io.penguin.penguinql.core.prune.FieldUtils.unWrapCollection;

public class QueryUtil {

    public static Query extractWholeQuery(Class<?> clazz) {
        Query query = new Query();
        Arrays.stream(clazz.getDeclaredFields())
                .forEach(i -> {
                    if (PRIMITIVES.contains(clazz)) {
                        if (query.getCurrent() == null) {
                            query.setCurrent(new HashSet<>());
                        }
                        query.getCurrent().add(i.getName());
                    } else {
                        if (query.getNext() == null) {
                            query.setNext(new HashMap<>());
                        }

                        Class<?> unWrapped = unWrapCollection(i);
                        query.getNext().put(i.getName(), extractWholeQuery(unWrapped));
                    }
                });
        return query;
    }
}
