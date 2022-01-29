package io.penguin.springboot.starter.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.penguin.pengiunlettuce.LettuceCache;
import io.penguin.pengiunlettuce.cofig.LettuceCacheConfig;
import io.penguin.pengiunlettuce.cofig.LettuceCacheIngredient;
import io.penguin.penguincore.reader.Reader;
import io.penguin.springboot.starter.yaml.Penguin;

import java.util.Map;
import java.util.Objects;

public class ContainerMapper {

    //private final Map<Container, Reader> mapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    enum ContainerKind {
        LETTUCE,
    }

    public ContainerMapper() {
        //mapper = new HashMap<>();
        //mapper.put(Container.LETTUCE)
    }

    public Reader generate(Penguin.Container container, Map<String, Reader> readers) {

        try {
            Objects.requireNonNull(container);

            Objects.requireNonNull(container.getKind());


            switch (ContainerKind.valueOf(container.getKind().toUpperCase())) {
                case LETTUCE:
                    LettuceCacheConfig config = objectMapper.convertValue(container.getSpec(), LettuceCacheConfig.class);
                    return new LettuceCache<>(LettuceCacheIngredient.toInternal(config, readers));
                default:
                    throw new IllegalStateException("No such Kind");
            }
        } catch (Exception e) {
            throw new IllegalStateException("Cannot create Reader");
        }
    }
}
