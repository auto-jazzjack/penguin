package io.penguin.penguinkafka.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Set;

@Data
@Builder
public class RedisProps {

    private Long expireTime;

    @NonNull
    private Set<String> redisHosts;
}
