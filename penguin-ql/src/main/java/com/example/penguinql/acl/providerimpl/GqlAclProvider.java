package com.example.penguinql.acl.providerimpl;

import com.example.penguinql.acl.AclProvider;
import com.example.penguinql.acl.exception.NotAuthorizationException;
import com.example.penguinql.core.GqlParser;
import com.example.penguinql.core.Query;

import java.util.HashMap;
import java.util.Map;

public class GqlAclProvider implements AclProvider {

    private final Map<String, Query> authMap;


    public GqlAclProvider() {
        authMap = new HashMap<>();
    }

    @Override
    public Query getAclAuth(String consumer) {
        return authMap.get(consumer);
    }

    @Override
    public void append(String consumer, String content) throws NotAuthorizationException {
        authMap.put(consumer, parse(content));
    }

    @Override
    public Query parse(String content) throws NotAuthorizationException {
        try {
            return GqlParser.DEFAULT.parseFrom(content);
        } catch (Exception p) {
            throw new NotAuthorizationException(p.getMessage());
        }
    }
}
