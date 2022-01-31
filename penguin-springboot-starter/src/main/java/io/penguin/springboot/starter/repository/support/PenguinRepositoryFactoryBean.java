package io.penguin.springboot.starter.repository.support;

import io.penguin.springboot.starter.PenguinConfiguration;
import io.penguin.springboot.starter.config.PenguinProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.support.RepositoryFactoryBeanSupport;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

@EnableConfigurationProperties(PenguinProperties.class)
public class PenguinRepositoryFactoryBean<T extends Repository<S, ID>, S, ID> extends RepositoryFactoryBeanSupport<T, S, ID> {

    private final PenguinConfiguration penguinProperties;

    public PenguinRepositoryFactoryBean(Class<? extends T> repositoryInterface, PenguinConfiguration penguinProperties) {
        super(repositoryInterface);
        this.penguinProperties = penguinProperties;
    }

    @Override
    public RepositoryFactorySupport createRepositoryFactory() {
        return new PenguinRepositoryFactory(penguinProperties);
    }
}
