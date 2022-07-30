package io.penguin.springboot.starter.mapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.penguin.pengiuncassandra.CassandraSource;
import io.penguin.pengiuncassandra.config.CassandraSourceConfig;
import io.penguin.pengiuncassandra.connection.CassandraConnectionConfig;
import io.penguin.pengiuncassandra.connection.CassandraConnectionIngredient;
import io.penguin.pengiunlettuce.LettuceCache;
import io.penguin.pengiunlettuce.cofig.LettuceCacheConfig;
import io.penguin.pengiunlettuce.cofig.LettuceCacheIngredient;
import io.penguin.pengiunlettuce.connection.LettuceConnectionConfig;
import io.penguin.pengiunlettuce.connection.LettuceConnectionIngredient;
import io.penguin.penguincore.reader.BaseOverWriteReader;
import io.penguin.penguincore.reader.Reader;
import io.penguin.penguincore.util.Pair;
import io.penguin.springboot.starter.Penguin;
import io.penguin.springboot.starter.config.PenguinProperties;
import io.penguin.springboot.starter.config.Validator;
import io.penguin.springboot.starter.factoy.ReaderFactory;
import io.penguin.springboot.starter.kind.BaseDeployment;
import io.penguin.springboot.starter.model.MultiBaseOverWriteReaders;
import io.penguin.springboot.starter.model.ReaderBundle;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.Constructor;
import java.util.*;
import java.util.stream.Collectors;

import static io.penguin.pengiuncassandra.config.CassandraIngredient.toInternal;
import static io.penguin.springboot.starter.mapper.ContainerKind.*;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ComponentCreator {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private Map<String, ReaderBundle> readers;
    private final PenguinProperties penguinProperties;
    private Map<String, Map<String, Object>> collectedResources;
    private final List<ReaderFactory> factories;
    private Map<ContainerKind, ReaderFactory> factoriesByKind;


    @PostConstruct
    public void init() {
        this.readers = new HashMap<>();
        Validator.validate(this.penguinProperties);

        collectedResources = this.penguinProperties.getSpec()
                .getResources()
                .stream()
                .collect(Collectors.toMap(PenguinProperties.Resource::getName, i -> Optional.of(i).map(PenguinProperties.Resource::getSpec).orElse(Collections.emptyMap())
                ));

        factoriesByKind = this.factories.stream()
                .collect(Collectors.toMap(ReaderFactory::getContainerType, i -> i));

        this.penguinProperties.getSpec()
                .getWorkers()
                .stream()
                .flatMap(i -> i.getContainers().stream())
                .forEach(i -> readers.put(i.getName(), generate(i)));
    }

    public ReaderBundle generate(PenguinProperties.Container container) {

        try {
            Objects.requireNonNull(container);
            Objects.requireNonNull(container.getKind());

            Map<String, Reader> flattenReader = flatten(readers);
            switch (ContainerKind.valueOf(container.getKind().toUpperCase())) {
                case LETTUCE_CACHE:
                    LettuceCacheConfig config = objectMapper.convertValue(container.getSpec(), LettuceCacheConfig.class);
                    LettuceConnectionConfig connection = objectMapper.convertValue(collectedResources.get(LETTUCE_CACHE.name()), LettuceConnectionConfig.class);

                    return ReaderBundle.builder()
                            .reader(new LettuceCache(LettuceConnectionIngredient.toInternal(connection), LettuceCacheIngredient.toInternal(config, flattenReader)))
                            .kind(ContainerKind.LETTUCE_CACHE)
                            .build();

                case HELLO:
                    return ReaderBundle.builder()
                            .reader(new HelloReader())
                            .kind(HELLO)
                            .build();
                case CASSANDRA:
                    CassandraSourceConfig cassandraSourceConfig = objectMapper.convertValue(container.getSpec(), CassandraSourceConfig.class);
                    CassandraConnectionConfig cassandra = objectMapper.convertValue(collectedResources.get(CASSANDRA.name()), CassandraConnectionConfig.class);

                    return ReaderBundle.builder()
                            .kind(CASSANDRA)
                            .reader(new CassandraSource(CassandraConnectionIngredient.toInternal(cassandra), toInternal(cassandraSourceConfig)))
                            .build();
                case OVER_WRITER:
                    Map<String, Class<? extends BaseOverWriteReader>> overWriters = objectMapper.convertValue(container.getSpec(), new TypeReference<>() {
                    });
                    return ReaderBundle.builder()
                            .kind(OVER_WRITER)
                            .reader(createReader(overWriters))
                            .build();

                case BEAN:
                    return ReaderBundle.builder()
                            .kind(BEAN)
                            .reader(factoriesByKind.get(BEAN).generate(container.getSpec()))
                            .build();
                default:
                    throw new IllegalStateException("No such Kind");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException("Cannot create Reader " + e);
        }
    }


    public Penguin generate(PenguinProperties.Worker worker) {

        try {
            Objects.requireNonNull(worker);
            Objects.requireNonNull(worker.getKind());

            switch (WorkerKind.valueOf(worker.getKind().toUpperCase())) {
                case BASE:
                    return new BaseDeployment(worker, readers, worker.getName());
                default:
                    throw new IllegalStateException("No such Kind");
            }
        } catch (Exception e) {
            throw new IllegalStateException("Cannot create Reader");
        }
    }

    private Map<String, Reader> flatten(Map<String, ReaderBundle> map) {
        return map.entrySet()
                .stream()
                .map(i -> Pair.of(i.getKey(), i.getValue().getReader()))
                .collect(Collectors.toMap(Pair::getKey, Pair::getValue));
    }

    private Reader createReader(Map<String, Class<? extends BaseOverWriteReader>> readers) {

        Map<Class<? extends BaseOverWriteReader>, BaseOverWriteReader> value = readers
                .values()
                .stream()
                .map(aClass -> {
                    try {
                        Constructor declaredConstructor = aClass.getDeclaredConstructor();
                        return Pair.of(aClass, (BaseOverWriteReader) declaredConstructor.newInstance());
                    } catch (Exception e) {
                        throw new IllegalStateException();
                    }
                })
                .collect(Collectors.toMap(Pair::getKey, Pair::getValue));

        return new MultiBaseOverWriteReaders(value);
    }
}
