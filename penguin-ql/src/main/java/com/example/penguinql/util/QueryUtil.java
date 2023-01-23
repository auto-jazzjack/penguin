package com.example.penguinql.util;

import com.example.penguinql.core.Query;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static com.example.penguinql.core.prune.FieldUtils.PRIMITIVES;
import static com.example.penguinql.core.prune.FieldUtils.unWrapCollection;

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
