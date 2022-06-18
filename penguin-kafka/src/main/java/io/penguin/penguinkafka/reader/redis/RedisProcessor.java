package io.penguin.penguinkafka.reader.redis;

import io.lettuce.core.RedisURI;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.penguin.pengiunlettuce.connection.RedisConnection;
import io.penguin.penguinkafka.model.KafkaProps;
import io.penguin.penguinkafka.reader.KafkaProcessor;

import java.util.List;
import java.util.stream.Collectors;

public class RedisProcessor extends KafkaProcessor<String, byte[]> {

    private final StatefulRedisClusterConnection<String, byte[]> connection;
    private final long expire;

    public RedisProcessor(KafkaProps kafkaProps) {
        super(kafkaProps);

        List<RedisURI> redisURIS = kafkaProps.getRedisProps()
                .getRedisHosts()
                .stream()
                .map(RedisURI::create)
                .collect(Collectors.toList());

        connection = RedisConnection.connection(redisURIS);
        expire = kafkaProps.getRedisProps().getExpireTime();
    }

    @Override
    public void action(String key, byte[] value) {
        connection.reactive()
                .setex(key, expire, value)
                .subscribe();
    }

    @Override
    public byte[] deserialize(byte[] bytes) {
        return bytes;
    }


}
