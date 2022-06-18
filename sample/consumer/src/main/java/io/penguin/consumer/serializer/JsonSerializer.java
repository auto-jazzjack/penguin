package io.penguin.consumer.serializer;

import io.penguin.penguinkafka.config.CommonConfiguration;
import io.penguin.penguinkafka.reader.KafkaManager;
import io.penguin.penguinkafka.reader.KafkaProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
public class JsonSerializer {

    private final CommonConfiguration commonConfiguration;
    private final KafkaManager kafkaProcessor;

    @PostConstruct
    public void asd() {
        System.out.println(commonConfiguration);
        System.out.println(kafkaProcessor);
    }
}
