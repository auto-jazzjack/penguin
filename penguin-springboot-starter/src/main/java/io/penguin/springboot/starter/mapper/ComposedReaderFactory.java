package io.penguin.springboot.starter.mapper;

import io.penguin.penguincore.reader.CacheContext;
import io.penguin.penguincore.reader.Reader;
import io.penguin.penguincore.util.Pair;
import io.penguin.springboot.starter.Penguin;
import io.penguin.springboot.starter.config.PenguinProperties;
import io.penguin.springboot.starter.config.Validator;
import io.penguin.springboot.starter.factoy.ReaderFactory;
import io.penguin.springboot.starter.kind.BaseDeployment;
import io.penguin.springboot.starter.model.ReaderBundle;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static io.penguin.springboot.starter.mapper.ContainerKind.LETTUCE_CACHE;
import static io.penguin.springboot.starter.mapper.ContainerKind.valueOf;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ComposedReaderFactory {

    private Map<String, ReaderBundle<Object, Object>> readers;
    private final PenguinProperties penguinProperties;
    private final List<ReaderFactory> factories;
    private Map<ContainerKind, ReaderFactory> factoriesByKind;


    @PostConstruct
    public void init() {
        this.readers = new HashMap<>();
        Validator.validate(this.penguinProperties);

        factoriesByKind = this.factories
                .stream()
                .collect(Collectors.toMap(ReaderFactory::getContainerType, i -> i));

        this.penguinProperties.getSpec()
                .getWorkers()
                .stream()
                .flatMap(i -> i.getContainers().stream())
                .forEach(i -> readers.put(i.getName(), generate(i)));
    }

    public ReaderBundle<Object, Object> generate(PenguinProperties.Container container) {

        try {
            Objects.requireNonNull(container);
            Objects.requireNonNull(container.getKind());

            Map<String, Reader<Object, CacheContext<Object>>> flattenReader = flatten(readers);
            ContainerKind containerKind = valueOf(container.getKind().toUpperCase());
            switch (containerKind) {
                case LETTUCE_CACHE:

                    return ReaderBundle.builder()
                            .reader(factoriesByKind.get(LETTUCE_CACHE).generateWithReaderPool(container.getSpec(), flattenReader))
                            .kind(ContainerKind.LETTUCE_CACHE)
                            .build();
                case CASSANDRA:
                case OVER_WRITER:
                case BEAN:
                    return ReaderBundle.builder()
                            .kind(containerKind)
                            .reader(factoriesByKind.get(containerKind).generate(container.getSpec()))
                            .build();
                default:
                    throw new IllegalStateException("No such Kind");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException("Cannot create Reader " + e);
        }
    }


    public Penguin<?, ?> generate(PenguinProperties.Worker worker) {

        try {
            Objects.requireNonNull(worker);
            Objects.requireNonNull(worker.getKind());

            switch (WorkerKind.valueOf(worker.getKind().toUpperCase())) {
                case BASE:
                    return new BaseDeployment<>(worker, readers, worker.getName());
                default:
                    throw new IllegalStateException("No such Kind");
            }
        } catch (Exception e) {
            throw new IllegalStateException("Cannot create Reader");
        }
    }

    private Map<String, Reader<Object, CacheContext<Object>>> flatten(Map<String, ReaderBundle<Object, Object>> map) {
        return map.entrySet()
                .stream()
                .map(i -> Pair.of(i.getKey(), i.getValue().getReader()))
                .collect(Collectors.toMap(Pair::getKey, Pair::getValue));
    }


}
