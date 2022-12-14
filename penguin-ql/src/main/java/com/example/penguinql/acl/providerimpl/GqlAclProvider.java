package com.example.penguinql.acl.providerimpl;

import com.example.penguinql.acl.AclProvider;
import com.example.penguinql.core.GqlParser;
import com.example.penguinql.core.Query;
import com.example.penguinql.exception.InvalidQueryException;
import com.example.penguinql.exception.NotAuthorizationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

public class GqlAclProvider implements AclProvider {

    private final Map<String, Query> authMap;
    private final GqlParser gqlParser;
    private static final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    public GqlAclProvider(GqlParser gqlParser) {
        this.authMap = new HashMap<>();
        this.gqlParser = gqlParser;
    }

    @Override
    public Query getAclAuth(String consumer) {
        return authMap.get(consumer);
    }


    @Override
    public void parseAndPut(String content) throws Throwable {
        try {
            Yaml yaml = mapper.readValue(content, Yaml.class);
            for (Map.Entry<String, String> entry : yaml.getV1().entrySet()) {
                String k = entry.getKey();
                String v = entry.getValue();
                Query query = this.gqlParser.parseFrom(v);
                this.authMap.put(k, query);
            }

        } catch (JsonProcessingException | InvalidQueryException e) {
            throw e;
        } catch (Throwable p) {
            throw new NotAuthorizationException(p.getMessage());
        }
    }

    @Data
    @NoArgsConstructor
    public static class Yaml {
        private Map<String, String> v1;
    }

}
