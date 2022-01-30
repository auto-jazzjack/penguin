package io.penguin.springboot.starter.config;

import io.penguin.springboot.starter.Deployment;
import io.penguin.springboot.starter.mapper.ComponentCreator;
import io.penguin.springboot.starter.model.ReaderBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.*;


@Configuration
public class ReaderTemplateConfiguration {

    private final Penguin penguin;
    private final ComponentCreator mapper = new ComponentCreator();
    private final List<Deployment> deploymentList;
    private Map<String, ReaderBundle> readers;

    public ReaderTemplateConfiguration(@Autowired Penguin penguin, @Autowired List<Deployment> deploymentList) {
        this.penguin = penguin;
        this.deploymentList = deploymentList;

        System.out.println(penguin);
        Validator.validate(penguin);

        readers = new TreeMap<>();//Order maintained

        Optional.of(penguin)
                .map(Penguin::getSpec)
                .map(Penguin.Spec::getContainers)
                .orElse(Collections.emptyList())
                .forEach(i -> readers.put(i.getName(), mapper.generate(i, readers)));

        add(mapper.generate(penguin, readers));
    }

    @Bean
    public List<Deployment> deployments() {
        return new ArrayList<>();
    }

    public void add(Deployment deployment) {
        deploymentList.add(deployment);
    }
}
