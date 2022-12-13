package com.example.penguinql.core;

import com.example.penguinql.acl.exception.NotAuthorizationException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Query {
    private Map<String, Query> next;
    private Set<String> current;

    public void checkAuth(Query query) throws NotAuthorizationException {
        throw new UnsupportedOperationException();
    }

    public Query merge(Query query) {
        if (this.getNext() == null) {
            this.setNext(new HashMap<>());
            next.forEach((k, v) -> this.getNext().put(k, merge(v)));
        }

        if (this.getCurrent() == null) {
            this.setCurrent(new HashSet<>());
            this.getCurrent().addAll(current);
        }

        return this;
    }



}
