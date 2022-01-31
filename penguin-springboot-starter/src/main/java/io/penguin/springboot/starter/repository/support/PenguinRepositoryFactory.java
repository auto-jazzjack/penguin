package io.penguin.springboot.starter.repository.support;

import io.penguin.springboot.starter.Penguin;
import io.penguin.springboot.starter.config.PenguinProperties;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

public class PenguinRepositoryFactory extends RepositoryFactorySupport {

    private final PenguinProperties penguinProperties;

    public PenguinRepositoryFactory(PenguinProperties penguinProperties) {
        this.penguinProperties = penguinProperties;
    }

    @Override
    public <T, ID> EntityInformation<T, ID> getEntityInformation(Class<T> domainClass) {
        return null;
    }

    protected Object getTargetRepository(RepositoryInformation repositoryInformation) {
        //EntityInformation<?, ?> entityInformation = this.getEntityInformation(repositoryInformation.getDomainType());

        //return null;
        return new Penguin<String, Map<String, String>>() {
            @Override
            public Mono<Map<String, String>> findOne(String key) {
                Map<String, String> retv = new HashMap<>();
                retv.put("asdasd", "asdasd");
                return Mono.just(retv);
            }
        };
        //return super.getTargetRepositoryViaReflection(repositoryInformation, new Object[]{entityInformation, this.keyValueOperations});
    }

    @Override
    protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
        return metadata.getRepositoryInterface();
    }
}