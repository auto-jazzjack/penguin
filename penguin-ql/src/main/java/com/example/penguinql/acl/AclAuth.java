package com.example.penguinql.acl;

import com.example.penguinql.acl.exception.NotAuthorizationException;
import com.example.penguinql.core.Query;

import java.util.Map;


public class AclAuth {

    public void contains(Query auth, Query client) throws NotAuthorizationException {

        for (String i : client.getCurrent()) {
            if (!auth.getCurrent().contains(i)) {
                throw new NotAuthorizationException();
            }
        }

        for (Map.Entry<String, Query> i : client.getNext().entrySet()) {
            if (!auth.getNext().containsKey(i.getKey())) {
                throw new NotAuthorizationException();
            } else {
                auth.getNext().get(i.getKey()).checkAuth(i.getValue());
            }

        }
    }

}
