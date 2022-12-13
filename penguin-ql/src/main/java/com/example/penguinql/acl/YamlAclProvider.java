package com.example.penguinql.acl;

import com.example.penguinql.acl.exception.NotAuthorizationException;

import java.util.HashMap;
import java.util.Map;

public class YamlAclProvider implements AclProvider {

    private final Map<String, AclAuth> authMap;

    public YamlAclProvider() {
        this.authMap = new HashMap<>();
    }

    @Override
    public AclAuth getAclAuth(String consumer) {
        return authMap.get(consumer);
    }

    @Override
    public void append(String consumer, String content) throws NotAuthorizationException {
        authMap.put(consumer, parse(content));
    }

    @Override
    public AclAuth parse(String content) throws NotAuthorizationException {
        return null;
    }
}
