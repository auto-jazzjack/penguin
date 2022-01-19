package io.penguin.penguincore.reader;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.penguin.pengiunlettuce.LettuceCache;
import io.penguin.pengiunlettuce.LettuceCacheConfig;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ObjectMapperCache extends LettuceCache<String, Map<String, String>> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public ObjectMapperCache(Reader<String, Map<String, String>> fromDownStream,
                             StatefulRedisClusterConnection<String, byte[]> connection, LettuceCacheConfig pureLettuceCacheConfig) throws Exception {
        super(fromDownStream, connection, pureLettuceCacheConfig);
    }

    @Override
    public byte[] serialize(Map<String, String> stringStringMap) {
        try {
            return objectMapper.writeValueAsBytes(stringStringMap);
        } catch (Exception e) {
            return new byte[0];
        }
    }

    @Override
    public Map<String, String> deserialize(byte[] bytes) {

        try {
            Thread.sleep(1000);
            return objectMapper.readValue(bytes, new TypeReference<>() {
            });
        } catch (Exception e) {
            System.out.println("Error");
            return Collections.emptyMap();
        }
    }

    @Override
    public Map<String, String> failFindOne(String key) {
        Map<String, String> retv = new HashMap<>();
        retv.put("fallback", "fallback");
        return retv;
    }
}
