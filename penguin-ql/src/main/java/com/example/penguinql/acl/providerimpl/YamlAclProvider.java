package com.example.penguinql.acl.providerimpl;

import com.example.penguinql.acl.AclAuth;
import com.example.penguinql.acl.AclProvider;
import com.example.penguinql.acl.exception.NotAuthorizationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.util.HashMap;
import java.util.Map;

public class YamlAclProvider implements AclProvider {

    private final Map<String, AclAuth> authMap;
    private static final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

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
        try {
            return mapper.readValue(content, AclAuth.class);
        } catch (Exception p) {
            throw new NotAuthorizationException(p.getMessage());
        }
    }
}
