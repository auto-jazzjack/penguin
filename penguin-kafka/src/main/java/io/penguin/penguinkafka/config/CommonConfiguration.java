package io.penguin.penguinkafka.config;

import io.penguin.penguinkafka.model.KafkaProps;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "process")
public class CommonConfiguration {
    private KafkaProps kafkaProps;
}
