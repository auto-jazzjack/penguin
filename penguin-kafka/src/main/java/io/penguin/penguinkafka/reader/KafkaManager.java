package io.penguin.penguinkafka.reader;

import io.penguin.penguinkafka.config.CommonConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class KafkaManager {

    private Map<String, KafkaProcessor> kafkaProcessorMap;
    private final CommonConfiguration properties;

    @PostConstruct
    public void init() {
        System.out.println();

    }

    public Map<String, Integer> revive() {
        Map<String, Integer> retv = new HashMap<>();
        for (Map.Entry<String, KafkaProcessor> entry : kafkaProcessorMap.entrySet()) {
            KafkaProcessor value = entry.getValue();
            int consume = value.consume();

            if (retv.get(entry.getKey()) == null) {
                retv.put(entry.getKey(), consume);
            } else {
                retv.put(entry.getKey(), retv.get(entry.getKey()) + 1);
            }
        }

        return retv;
    }

    public Map<String, Long> rewind() {
        return Collections.emptyMap();
    }
}
