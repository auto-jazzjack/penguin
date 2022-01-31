package io.penguin.springboot.starter;

import io.penguin.springboot.starter.config.PenguinProperties;
import io.penguin.springboot.starter.repository.configuration.PenguinRepositoriesRegistrar;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.annotation.PostConstruct;


@Configuration
@EnableConfigurationProperties(PenguinProperties.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PenguinConfiguration {
    private final PenguinProperties penguinProperties;

    @PostConstruct
    public void init() {
        System.out.println(penguinProperties);
        System.out.println("hello");
    }

}
