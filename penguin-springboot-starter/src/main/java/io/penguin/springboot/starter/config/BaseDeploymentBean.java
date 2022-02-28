package io.penguin.springboot.starter.config;

import io.penguin.penguincore.util.Pair;
import io.penguin.springboot.starter.Penguin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class BaseDeploymentBean {

    @Bean
    public Map<String, Penguin> penguinMap(List<Penguin> baseDeployment) {
        return baseDeployment.stream()
                .map(i -> Pair.of(i.getName(), i))
                .collect(Collectors.toMap(Pair::getKey, Pair::getValue));
    }
}
