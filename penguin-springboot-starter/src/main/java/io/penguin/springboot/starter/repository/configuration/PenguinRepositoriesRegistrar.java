package io.penguin.springboot.starter.repository.configuration;

import org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;

import java.lang.annotation.Annotation;

public class PenguinRepositoriesRegistrar extends AbstractRepositoryConfigurationSourceSupport {

    @Override
    protected Class<? extends Annotation> getAnnotation() {
        return EnablePenguinRepositories.class;
    }


    @Override
    protected Class<?> getConfiguration() {
        //return PenguinConfig.class;
        return EnableObjectifyRepositoriesConfiguration.class;
    }

    @Override
    protected RepositoryConfigurationExtension getRepositoryConfigurationExtension() {
        return new PenguinRepositoryConfigurationExtension();
    }

    @EnablePenguinRepositories
    private static class EnableObjectifyRepositoriesConfiguration {
    }
}
