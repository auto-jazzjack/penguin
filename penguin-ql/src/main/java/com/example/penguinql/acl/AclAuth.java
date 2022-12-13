package com.example.penguinql.acl;

import com.example.penguinql.acl.exception.NotAuthorizationException;
import com.example.penguinql.core.Query;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
@AllArgsConstructor
@Builder
public class AclAuth {
    private Map<String, AclAuth> next;
    private Set<String> current;

    public void checkAuth(Query query) throws NotAuthorizationException {

        for (String i : query.getFields()) {
            if (!current.contains(i)) {
                throw new NotAuthorizationException();
            }
        }

        for (Map.Entry<String, Query> i : query.getQueryByResolverName().entrySet()) {
            if (!next.containsKey(i.getKey())) {
                throw new NotAuthorizationException();
            } else {
                next.get(i.getKey()).checkAuth(i.getValue());
            }

        }
    }

    public AclAuth merge(AclAuth aclAuth) {

        if (next == null) {
            next = new HashMap<>();
            aclAuth.getNext().forEach((k, v) -> next.put(k, merge(v)));
        }

        if (current == null) {
            current = new HashSet<>();
            current.addAll(aclAuth.getCurrent());
        }

        return this;
    }
}
