package io.penguin.springboot.starter.factory;

import io.penguin.springboot.starter.ReaderTemplateConfiguration;
import io.penguin.springboot.starter.mapper.ComponentCreator;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.ReactiveRepositoryFactorySupport;
import org.springframework.data.repository.core.support.RepositoryFactoryBeanSupport;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

public class RepositoryFactory<T extends Repository<S, ID>, S, ID> extends RepositoryFactoryBeanSupport<T, S, ID> {

    private final ReaderTemplateConfiguration configuration;
    private final ComponentCreator componentCreator = new ComponentCreator();

    public RepositoryFactory(Class<? extends T> repositoryInterface, ReaderTemplateConfiguration configuration) {
        super(repositoryInterface);
        this.configuration = configuration;
    }

    @Override
    protected RepositoryFactorySupport createRepositoryFactory() {
        return new ReactiveRepositoryFactorySupport() {
            @Override
            public <T, ID> EntityInformation<T, ID> getEntityInformation(Class<T> domainClass) {
                return null;
            }

            @Override
            protected Object getTargetRepository(RepositoryInformation metadata) {
                return componentCreator.generate(configuration.getPenguin(), configuration.getReaders());
            }

            @Override
            protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
                return metadata.getRepositoryInterface();
            }
        };
    }
}