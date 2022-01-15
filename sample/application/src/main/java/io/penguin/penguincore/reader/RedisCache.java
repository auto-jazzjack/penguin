package io.penguin.penguincore.reader;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.penguin.pengiunlettuce.LettuceCacheConfig;
import io.penguin.pengiunlettuce.LettuceCacheWithPlugin;

import java.util.Collections;
import java.util.Map;

public class RedisCache extends LettuceCacheWithPlugin<String, Map<String, String>> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public RedisCache(Reader<String, Map<String, String>> fromDownStream,
                      StatefulRedisClusterConnection<String, byte[]> connection, LettuceCacheConfig pureLettuceCacheConfig) throws Exception{
        super(fromDownStream, connection, pureLettuceCacheConfig, null);
    }

    @Override
    public byte[] serialize(Map<String, String> stringStringMap) {
        try {
            return objectMapper.writeValueAsBytes(stringStringMap);
        } catch (Exception e) {
            System.out.println("Error");
            return new byte[0];
        }
    }

    @Override
    public Map<String, String> deserialize(byte[] bytes) {

        try {
            Map<String, String> stringStringMap = objectMapper.readValue(bytes, new TypeReference<Map<String, String>>() {
            });
            return stringStringMap;
        } catch (Exception e) {
            System.out.println("Error");
            return Collections.emptyMap();
        }
    }
}
