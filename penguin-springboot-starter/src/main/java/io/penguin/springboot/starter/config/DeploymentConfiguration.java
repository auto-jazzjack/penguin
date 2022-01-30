package io.penguin.springboot.starter.config;

import io.penguin.springboot.starter.Deployment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class DeploymentConfiguration {

    @Bean
    public List<Deployment> deployments() {
        return new ArrayList<>();
    }
}
