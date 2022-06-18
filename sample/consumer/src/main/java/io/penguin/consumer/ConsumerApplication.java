package io.penguin.consumer;

import io.penguin.penguinkafka.config.CommonConfiguration;
import io.penguin.penguinkafka.reader.KafkaManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@Import({CommonConfiguration.class, KafkaManager.class})
@SpringBootApplication
public class ConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConsumerApplication.class, args);
    }

}
