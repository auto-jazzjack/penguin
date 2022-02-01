package io.penguin.springboot.starter.repository.support;

import io.penguin.penguincore.util.Pair;
import io.penguin.springboot.starter.config.PenguinProperties;
import io.penguin.springboot.starter.mapper.ComponentCreator;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import java.util.Map;
import java.util.stream.Collectors;

public class PenguinRepositoryFactory extends RepositoryFactorySupport {

    private final ComponentCreator creator;
    private final Map<String, PenguinProperties.Worker> workerByName;

    public PenguinRepositoryFactory(PenguinProperties penguinProperties) {
        creator = new ComponentCreator(penguinProperties);
        workerByName = penguinProperties.getSpec().getWorkers()
                .stream()
                .map(i -> Pair.of(i.getAggregatedTarget().getName(), i))
                .collect(Collectors.toMap(Pair::getKey, Pair::getValue));
    }

    @Override
    public <T, ID> EntityInformation<T, ID> getEntityInformation(Class<T> domainClass) {
        return null;
    }

    protected Object getTargetRepository(RepositoryInformation repositoryInformation) {
        return creator.generate(workerByName.get(repositoryInformation.getRepositoryInterface().getName()));
    }


    @Override
    protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
        return metadata.getRepositoryInterface();
    }
}