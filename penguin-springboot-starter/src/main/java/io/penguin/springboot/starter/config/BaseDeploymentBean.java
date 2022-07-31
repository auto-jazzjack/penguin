package io.penguin.springboot.starter.config;

import io.penguin.penguincore.util.Pair;
import io.penguin.springboot.starter.Penguin;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BaseDeploymentBean {

    private final PenguinProperties penguinProperties;

    @Bean
    public Map<String, Penguin> penguinMap(List<Penguin> baseDeployment) {
        return baseDeployment.stream()
                .map(i -> Pair.of(i.getName(), i))
                .collect(Collectors.toMap(Pair::getKey, Pair::getValue));
    }

    @Bean
    public Map<String, Map<String, Object>> collectedResources() {
        return penguinProperties.getSpec()
                .getResources()
                .stream()
                .collect(Collectors.toMap(PenguinProperties.Resource::getName, i -> Optional.of(i).map(PenguinProperties.Resource::getSpec).orElse(Collections.emptyMap())
                ));
    }

}
