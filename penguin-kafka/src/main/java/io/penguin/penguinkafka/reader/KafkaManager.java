package io.penguin.penguinkafka.reader;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

@Component
public class KafkaManager {

    private Map<String, KafkaProcessor> kafkaProcessorMap;

    public Map<String, Integer> revive() {
        kafkaProcessorMap.entrySet().forEach(i-> i.getValue().consume());
        return Collections.emptyMap();
    }

    public Map<String, Long> rewind() {
        return Collections.emptyMap();
    }
}
