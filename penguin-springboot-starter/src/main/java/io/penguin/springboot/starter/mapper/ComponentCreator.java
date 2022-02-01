package io.penguin.springboot.starter.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.penguin.pengiunlettuce.LettuceCache;
import io.penguin.pengiunlettuce.cofig.LettuceCacheConfig;
import io.penguin.pengiunlettuce.cofig.LettuceCacheIngredient;
import io.penguin.penguincore.reader.Reader;
import io.penguin.penguincore.util.Pair;
import io.penguin.springboot.starter.Penguin;
import io.penguin.springboot.starter.config.PenguinProperties;
import io.penguin.springboot.starter.config.Validator;
import io.penguin.springboot.starter.kind.BaseDeployment;
import io.penguin.springboot.starter.model.ReaderBundle;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static io.penguin.springboot.starter.mapper.ContainerKind.HELLO;
import static io.penguin.springboot.starter.mapper.ContainerKind.SOURCE;


public class ComponentCreator {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private Map<String, ReaderBundle> readers;
    private final PenguinProperties penguinProperties;


    public ComponentCreator(PenguinProperties penguinProperties) {
        this.penguinProperties = penguinProperties;
        this.readers = new HashMap<>();
        Validator.validate(this.penguinProperties);
        init();

    }


    private void init() {
        this.penguinProperties.getSpec()
                .getContainers()
                .forEach(i -> readers.put(i.getName(), generate(i)));
    }

    public ReaderBundle generate(PenguinProperties.Container container) {

        try {
            Objects.requireNonNull(container);
            Objects.requireNonNull(container.getKind());

            switch (ContainerKind.valueOf(container.getKind().toUpperCase())) {
                case REMOTE_CACHE:
                    LettuceCacheConfig config = objectMapper.convertValue(container.getSpec(), LettuceCacheConfig.class);
                    return ReaderBundle.builder()
                            .reader(new LettuceCache<>(LettuceCacheIngredient.toInternal(config, flatten(readers))))
                            .kind(ContainerKind.REMOTE_CACHE)
                            .build();

                case HELLO:
                    return ReaderBundle.builder()
                            .reader(new HelloReader())
                            .kind(HELLO)
                            .build();
                case SOURCE:
                    return ReaderBundle.builder()
                            .kind(SOURCE)
                            .build();
                default:
                    throw new IllegalStateException("No such Kind");
            }
        } catch (Exception e) {
            throw new IllegalStateException("Cannot create Reader");
        }
    }

    public Penguin generate(PenguinProperties penguinProperties) {

        try {
            Objects.requireNonNull(penguinProperties);
            Objects.requireNonNull(penguinProperties.getKind());

            switch (DeploymentKind.valueOf(penguinProperties.getKind().toUpperCase())) {
                case BASE:
                    return new BaseDeployment(penguinProperties, readers);
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
}
