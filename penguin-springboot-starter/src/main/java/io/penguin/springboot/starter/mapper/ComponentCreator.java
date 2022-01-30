package io.penguin.springboot.starter.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.penguin.pengiunlettuce.LettuceCache;
import io.penguin.pengiunlettuce.cofig.LettuceCacheConfig;
import io.penguin.pengiunlettuce.cofig.LettuceCacheIngredient;
import io.penguin.penguincore.reader.Reader;
import io.penguin.penguincore.util.Pair;
import io.penguin.springboot.starter.Deployment;
import io.penguin.springboot.starter.config.Penguin;
import io.penguin.springboot.starter.kind.BaseDeployment;
import io.penguin.springboot.starter.model.ReaderBundle;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static io.penguin.springboot.starter.mapper.ContainerKind.HELLO;
import static io.penguin.springboot.starter.mapper.ContainerKind.SOURCE;


public class ComponentCreator {

    //private final Map<Container, Reader> mapper;
    private final ObjectMapper objectMapper = new ObjectMapper();


    public ComponentCreator() {
    }

    public ReaderBundle generate(Penguin.Container container, Map<String, ReaderBundle> readers) {

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

    public Deployment generate(Penguin penguin, Map<String, ReaderBundle> readerBundleMap) {

        try {
            Objects.requireNonNull(penguin);
            Objects.requireNonNull(penguin.getKind());

            switch (DeploymentKind.valueOf(penguin.getKind().toUpperCase())) {
                case BASE:
                    return new BaseDeployment(penguin, readerBundleMap);
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
