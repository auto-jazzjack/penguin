package io.penguin.springboot.starter.repository.support;

import io.penguin.springboot.starter.Penguin;
import io.penguin.springboot.starter.config.PenguinProperties;
import io.penguin.springboot.starter.mapper.ComponentCreator;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

public class PenguinRepositoryFactory extends RepositoryFactorySupport {

    private final ComponentCreator creator;
    private final PenguinProperties penguinProperties;

    public PenguinRepositoryFactory(PenguinProperties penguinProperties) {
        this.penguinProperties = penguinProperties;
        creator = new ComponentCreator(this.penguinProperties);
    }

    @Override
    public <T, ID> EntityInformation<T, ID> getEntityInformation(Class<T> domainClass) {
        return null;
    }

    protected Object getTargetRepository(RepositoryInformation repositoryInformation) {

        /*repositoryInformation.getRepositoryBaseClass();
        repositoryInformation.getDomainType();
        repositoryInformation.getIdType();*/

        return creator.generate(this.penguinProperties);
    }

    @Override
    protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
        return metadata.getRepositoryInterface();
    }
}