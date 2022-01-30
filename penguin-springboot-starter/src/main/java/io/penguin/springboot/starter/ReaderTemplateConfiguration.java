package io.penguin.springboot.starter;

import io.penguin.springboot.starter.config.Penguin;
import io.penguin.springboot.starter.config.Validator;
import io.penguin.springboot.starter.mapper.ComponentCreator;
import io.penguin.springboot.starter.model.ReaderBundle;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.*;


@Data
@Configuration
public class ReaderTemplateConfiguration {

    private final Penguin penguin;
    private final ComponentCreator mapper = new ComponentCreator();
    private final List<Deployment> deployments;
    private Map<String, ReaderBundle> readers;

    public ReaderTemplateConfiguration(@Autowired Penguin penguin, List<Deployment> deployments) {
        this.penguin = penguin;
        this.deployments = deployments;

        //System.out.println(penguin);
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
        deployments.add(deployment);
    }
}
