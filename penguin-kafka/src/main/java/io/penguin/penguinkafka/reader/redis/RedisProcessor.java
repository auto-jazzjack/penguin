package io.penguin.penguinkafka.reader.redis;

import io.lettuce.core.RedisURI;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.penguin.pengiunlettuce.connection.RedisConnectionFactory;
import io.penguin.penguincore.reader.CacheContext;
import io.penguin.penguinkafka.model.KafkaProps;
import io.penguin.penguinkafka.reader.KafkaProcessor;

import java.util.List;
import java.util.stream.Collectors;

public class RedisProcessor extends KafkaProcessor<String, byte[]> {

    private final StatefulRedisClusterConnection<String, CacheContext<byte[]>> connection;
    private final long expire;

    public RedisProcessor(KafkaProps kafkaProps) {
        super(kafkaProps);

        List<RedisURI> redisURIS = kafkaProps.getRedisProps()
                .getRedisHosts()
                .stream()
                .map(RedisURI::create)
                .collect(Collectors.toList());

        //TODO: fix it
        connection = RedisConnectionFactory.connection(redisURIS, null);
        expire = kafkaProps.getRedisProps().getExpireTime();
    }

    @Override
    public void action(String key, byte[] value) {
        connection.reactive()
                .setex(key, expire, new CacheContext<byte[]>() {
                    @Override
                    public long getTimeStamp() {
                        return 0;
                    }

                    @Override
                    public byte[] getValue() {
                        return value;
                    }
                })
                .subscribe();
    }

    @Override
    public byte[] deserialize(byte[] bytes) {
        return bytes;
    }


}
