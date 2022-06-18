package io.penguin.penguinkafka.config;

import io.penguin.penguinkafka.model.KafkaProps;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "processor")
public class CommonConfiguration {

    private Map<String, KafkaProps> kafkaProps;
}
