package io.penguin.springboot.starter.config;

import io.penguin.springboot.starter.Deployment;
import io.penguin.springboot.starter.mapper.ComponentCreator;
import io.penguin.springboot.starter.model.ReaderBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;

import java.util.*;

@Order(1)
@Import({Penguin.class, DeploymentConfiguration.class})
@Configuration
public class ReaderTemplateConfiguration {

    private final Penguin penguin;
    private final ComponentCreator mapper = new ComponentCreator();
    private final List<Deployment> deploymentList;
    private Map<String, ReaderBundle> readers;

    public ReaderTemplateConfiguration(@Autowired Penguin penguin, List<Deployment> deploymentList) {
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


    public void add(Deployment deployment) {
        deploymentList.add(deployment);
    }
}
