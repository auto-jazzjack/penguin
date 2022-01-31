package io.penguin.springboot.starter.repository.support;

import io.penguin.springboot.starter.Penguin;
import io.penguin.springboot.starter.config.PenguinConfig;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import reactor.core.publisher.Mono;

public class PenguinRepositoryFactory extends RepositoryFactorySupport {

    private final PenguinConfig penguinConfig;

    public PenguinRepositoryFactory(PenguinConfig penguinConfig) {
        this.penguinConfig = penguinConfig;
    }

    @Override
    public <T, ID> EntityInformation<T, ID> getEntityInformation(Class<T> domainClass) {
        return null;
    }

    protected Object getTargetRepository(RepositoryInformation repositoryInformation) {
        //EntityInformation<?, ?> entityInformation = this.getEntityInformation(repositoryInformation.getDomainType());

        return new Penguin<>() {
            @Override
            public Mono<Object> findOne(Object key) {
                return Mono.just("hello");
            }
        };
        //return super.getTargetRepositoryViaReflection(repositoryInformation, new Object[]{entityInformation, this.keyValueOperations});
    }

    @Override
    protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
        return metadata.getRepositoryInterface();
    }
}