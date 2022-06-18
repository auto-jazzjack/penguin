package io.penguin.penguinkafka.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.apache.kafka.common.serialization.Serializer;

@Data
@Builder
public class KafkaProps {
    private Class<? extends Serializer<?>> keySerializer;

    @NonNull
    private String bootStrap;
    private String topic;
    private String groupId;
    private OffSet offset;
    private Long poll;
    private RedisProps redisProps;
    private Integer concurrency;

    enum OffSet {
        EARLIEST,
        LATEST,
    }


}
