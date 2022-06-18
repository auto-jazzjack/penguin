package io.penguin.penguinkafka.model;

import lombok.*;
import org.apache.kafka.common.serialization.Serializer;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
    private Class<? extends Actor> actor;

    enum OffSet {
        EARLIEST,
        LATEST,
    }


}
