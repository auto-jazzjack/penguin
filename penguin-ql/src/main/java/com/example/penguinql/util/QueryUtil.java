package com.example.penguinql.util;

import com.example.penguinql.core.Query;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class QueryUtil {

    public static Query extractWholeQuery(Class<?> clazz) {
        Query query = new Query();
        Arrays.stream(clazz.getDeclaredFields())
                .forEach(i -> {
                    if (i.getType().isPrimitive()) {
                        if (query.getCurrent() == null) {
                            query.setCurrent(new HashSet<>());
                        }
                        query.getCurrent().add(i.getName());
                    } else {
                        if (query.getNext() == null) {
                            query.setNext(new HashMap<>());
                        }
                        query.getNext().put(i.getName(), extractWholeQuery(i.getType()));
                    }
                });
        return query;
    }
}
