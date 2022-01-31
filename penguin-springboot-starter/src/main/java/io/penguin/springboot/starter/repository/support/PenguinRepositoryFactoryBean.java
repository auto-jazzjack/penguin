package io.penguin.springboot.starter.repository.support;

import io.penguin.springboot.starter.config.PenguinConfig;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.support.RepositoryFactoryBeanSupport;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;


public class PenguinRepositoryFactoryBean<T extends Repository<S, ID>, S, ID> extends RepositoryFactoryBeanSupport<T, S, ID> {

    private final PenguinConfig penguinConfig;

    public PenguinRepositoryFactoryBean(Class<? extends T> repositoryInterface, PenguinConfig penguinConfig) {
        super(repositoryInterface);
        this.penguinConfig = penguinConfig;
    }

    @Override
    public RepositoryFactorySupport createRepositoryFactory() {
        return new PenguinRepositoryFactory(penguinConfig);
    }
}
