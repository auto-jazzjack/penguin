package io.penguin.penguinkafka.reader;

import io.penguin.penguinkafka.config.CommonConfiguration;
import io.penguin.penguinkafka.model.Actor;
import io.penguin.penguinkafka.model.KafkaProps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.OffsetAndTimestamp;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

import static io.penguin.penguinkafka.util.KafkaUtil.createActor;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class KafkaManager {

    private Map<String, KafkaProcessor<?,?>> kafkaProcessorMap;
    private final CommonConfiguration properties;

    @PostConstruct
    public void init() {
        kafkaProcessorMap = new HashMap<>();

        for (Map.Entry<String, KafkaProps> i : properties.getKafkaProps().entrySet()) {
            Actor actor = createActor(i.getValue().getActor());
            kafkaProcessorMap.put(i.getKey(), new KafkaProcessor<>(i.getValue()) {
                @Override
                public void action(Object key, Object value) {
                    actor.action(key, value);
                }

                @Override
                public Object deserialize(byte[] bytes) {
                    return actor.deserialize(bytes);
                }
            });
        }
    }

    public Map<String, Integer> revive() {
        Map<String, Integer> retv = new HashMap<>();
        for (Map.Entry<String, KafkaProcessor<?,?>> entry : kafkaProcessorMap.entrySet()) {
            KafkaProcessor<?,?> value = entry.getValue();
            int consume = value.consume();

            if (retv.get(entry.getKey()) == null) {
                retv.put(entry.getKey(), consume);
            } else {
                retv.put(entry.getKey(), retv.get(entry.getKey()) + 1);
            }
        }

        return retv;
    }

    public Map<Integer, Long> rewind(String processor, Date date) {
        Map<TopicPartition, OffsetAndTimestamp> rewind = kafkaProcessorMap.get(processor).rewind(date);
        return Optional.ofNullable(rewind)
                .orElse(Collections.emptyMap())
                .entrySet()
                .stream()
                .collect(Collectors.toMap(i -> i.getKey().partition(), i -> i.getValue().timestamp()));
    }
}
