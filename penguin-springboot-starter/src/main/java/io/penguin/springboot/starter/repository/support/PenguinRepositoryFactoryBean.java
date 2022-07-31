package io.penguin.springboot.starter.repository.support;

import io.penguin.springboot.starter.config.PenguinProperties;
import io.penguin.springboot.starter.factoy.ReaderFactory;
import io.penguin.springboot.starter.mapper.ComposedReaderFactory;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.support.RepositoryFactoryBeanSupport;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

@Import({PenguinProperties.class, ComposedReaderFactory.class,})
@ComponentScan(basePackageClasses = ReaderFactory.class)
public class PenguinRepositoryFactoryBean<T extends Repository<S, ID>, S, ID> extends RepositoryFactoryBeanSupport<T, S, ID> {

    private final PenguinProperties penguinProperties;
    private final ComposedReaderFactory creator;

    public PenguinRepositoryFactoryBean(Class<? extends T> repositoryInterface,
                                        PenguinProperties penguinProperties,
                                        ComposedReaderFactory creator
    ) {
        super(repositoryInterface);
        this.penguinProperties = penguinProperties;
        this.creator = creator;
    }

    @Override
    public RepositoryFactorySupport createRepositoryFactory() {
        return new PenguinRepositoryFactory(penguinProperties, creator);
    }
}
