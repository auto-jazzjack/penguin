package io.penguin.springboot.starter.factoy;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.penguin.pengiunlettuce.LettuceCache;
import io.penguin.pengiunlettuce.cofig.LettuceCacheConfig;
import io.penguin.pengiunlettuce.cofig.LettuceCacheIngredient;
import io.penguin.pengiunlettuce.connection.LettuceConnectionConfig;
import io.penguin.pengiunlettuce.connection.LettuceConnectionIngredient;
import io.penguin.penguincore.reader.Context;
import io.penguin.penguincore.reader.Reader;
import io.penguin.springboot.starter.mapper.ContainerKind;
import lombok.Builder;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class RedisFactory implements ReaderFactory<RedisFactory.RedisFactoryInput> {
    private static final ObjectMapper objectMapper = new ObjectMapper();


    public RedisFactory() {
    }

    @Override
    public ContainerKind getContainerType() {
        return ContainerKind.LETTUCE_CACHE;
    }

    @Override
    public Reader<Object, Context<Object>> generate(RedisFactoryInput reidFactoryInput, Map<String, Object> spec) throws Exception {
        LettuceCacheConfig config = objectMapper.convertValue(spec, LettuceCacheConfig.class);

        return new LettuceCache<>(LettuceConnectionIngredient.toInternal(reidFactoryInput.getConnection()), LettuceCacheIngredient.toInternal(config, reidFactoryInput.getReaders()));
    }


    @Data
    @Builder
    static public class RedisFactoryInput {
        private LettuceConnectionConfig connection;
        private Map<String, Reader<Object, Context<Object>>> readers;

    }
}
