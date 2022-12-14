package com.example.penguinql.acl;

import com.example.penguinql.core.Query;
import com.example.penguinql.exception.NotAuthorizationException;

import java.util.Map;
import java.util.Objects;


public class AclAuth {

    public static void contains(Query auth, Query client) throws NotAuthorizationException {

        Objects.requireNonNull(auth, "Client does not have any auth");
        Objects.requireNonNull(client);

        for (String i : client.getCurrent()) {
            if (!auth.getCurrent().contains(i)) {
                throw new NotAuthorizationException("Client do not have auth on " + i);
            }
        }

        for (Map.Entry<String, Query> i : client.getNext().entrySet()) {
            if (!auth.getNext().containsKey(i.getKey())) {
                throw new NotAuthorizationException("Client do not have auth on " + i.getKey());
            } else {
                contains(auth.getNext().get(i.getKey()), i.getValue());
            }

        }
    }

}
