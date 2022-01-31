package io.penguin.springboot.starter.repository.configuration;

import org.springframework.data.repository.config.RepositoryBeanDefinitionRegistrarSupport;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;

import java.lang.annotation.Annotation;

/**
 * copied https://github.com/n15g/spring-boot-gae/blob/master/src/main/java/contrib/springframework/data/gcp/objectify/config/ObjectifyRepositoryConfigurationExtension.java
 */
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
