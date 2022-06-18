package io.penguin.penguinkafka.controller;

import io.penguin.penguinkafka.reader.KafkaManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController("/api/cluster")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ConfigController {
    private final KafkaManager kafkaManager;

    @PostMapping(value = "/revive", produces = "application/json")
    public Map<String, Integer> revive() {
        return kafkaManager.revive();
    }
}
