package io.penguin.springboot.starter.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.penguin.pengiuncassandra.connection.CassandraConnectionConfig;
import io.penguin.pengiunlettuce.connection.LettuceConnectionConfig;
import io.penguin.penguincore.reader.Context;
import io.penguin.penguincore.reader.Reader;
import io.penguin.penguincore.util.Pair;
import io.penguin.springboot.starter.Penguin;
import io.penguin.springboot.starter.config.PenguinProperties;
import io.penguin.springboot.starter.config.Validator;
import io.penguin.springboot.starter.factoy.ReaderFactory;
import io.penguin.springboot.starter.factoy.RedisFactory;
import io.penguin.springboot.starter.kind.BaseDeployment;
import io.penguin.springboot.starter.model.ReaderBundle;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

import static io.penguin.springboot.starter.mapper.ContainerKind.*;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ComposedReaderFactory {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private Map<String, ReaderBundle<Object, Object>> readers;
    private final PenguinProperties penguinProperties;
    private Map<String, Map<String, Object>> collectedResources;
    private final List<ReaderFactory<?>> factories;
    private Map<ContainerKind, ReaderFactory<Object>> factoriesByKind;


    @PostConstruct
    public void init() {
        this.readers = new HashMap<>();
        Validator.validate(this.penguinProperties);

        collectedResources = this.penguinProperties.getSpec()
                .getResources()
                .stream()
                .collect(Collectors.toMap(PenguinProperties.Resource::getName, i -> Optional.of(i).map(PenguinProperties.Resource::getSpec).orElse(Collections.emptyMap())
                ));

        factoriesByKind = this.factories
                .stream()
                .collect(Collectors.toMap(ReaderFactory::getContainerType, i -> (ReaderFactory) i));

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

            Map<String, Reader<Object, Context<Object>>> flattenReader = flatten(readers);
            ContainerKind containerKind = valueOf(container.getKind().toUpperCase());
            switch (containerKind) {
                case LETTUCE_CACHE:
                    LettuceConnectionConfig connection = objectMapper.convertValue(collectedResources.get(LETTUCE_CACHE.name()), LettuceConnectionConfig.class);
                    return ReaderBundle.builder()
                            .reader(factoriesByKind.get(LETTUCE_CACHE).generate(RedisFactory.RedisFactoryInput.builder()
                                    .connection(connection)
                                    .readers(flattenReader)
                                    .build(), container.getSpec()))
                            .kind(ContainerKind.LETTUCE_CACHE)
                            .build();
                case CASSANDRA:
                    CassandraConnectionConfig cassandra = objectMapper.convertValue(collectedResources.get(CASSANDRA.name()), CassandraConnectionConfig.class);
                    return ReaderBundle.builder()
                            .kind(CASSANDRA)
                            .reader(factoriesByKind.get(CASSANDRA).generate(cassandra, container.getSpec()))
                            .build();
                case OVER_WRITER:
                case BEAN:
                    return ReaderBundle.builder()
                            .kind(containerKind)
                            .reader(factoriesByKind.get(containerKind).generate(null, container.getSpec()))
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

    private Map<String, Reader<Object, Context<Object>>> flatten(Map<String, ReaderBundle<Object, Object>> map) {
        return map.entrySet()
                .stream()
                .map(i -> Pair.of(i.getKey(), i.getValue().getReader()))
                .collect(Collectors.toMap(Pair::getKey, Pair::getValue));
    }


}
