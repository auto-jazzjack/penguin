package io.penguin.springboot.starter.repository.support;

import io.penguin.springboot.starter.config.PenguinProperties;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.support.RepositoryFactoryBeanSupport;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;


public class PenguinRepositoryFactoryBean<T extends Repository<S, ID>, S, ID> extends RepositoryFactoryBeanSupport<T, S, ID> {

    private final PenguinProperties penguinProperties;

    public PenguinRepositoryFactoryBean(Class<? extends T> repositoryInterface, PenguinProperties penguinProperties) {
        super(repositoryInterface);
        this.penguinProperties = penguinProperties;
    }

    @Override
    public RepositoryFactorySupport createRepositoryFactory() {
        return new PenguinRepositoryFactory(penguinProperties);
    }
}
