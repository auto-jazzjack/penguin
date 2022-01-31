package io.penguin.springboot.starter.repository.configuration;

import org.springframework.data.repository.config.RepositoryBeanDefinitionRegistrarSupport;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;

import java.lang.annotation.Annotation;

public class PenguinRepositoriesRegistrar extends RepositoryBeanDefinitionRegistrarSupport {

    @Override
    protected Class<? extends Annotation> getAnnotation() {
        return EnablePenguinRepositories.class;
    }

    @Override
    protected RepositoryConfigurationExtension getExtension() {
        return new PenguinRepositoryConfigurationExtension();
    }
}
