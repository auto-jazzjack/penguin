package io.penguin.springboot.starter.factoy;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.penguin.pengiunlettuce.LettuceCache;
import io.penguin.pengiunlettuce.cofig.LettuceCacheConfig;
import io.penguin.pengiunlettuce.cofig.LettuceCacheIngredient;
import io.penguin.pengiunlettuce.connection.LettuceConnectionConfig;
import io.penguin.pengiunlettuce.connection.LettuceConnectionIngredient;
import io.penguin.penguincore.reader.BaseOverWriteReader;
import io.penguin.penguincore.reader.Context;
import io.penguin.penguincore.reader.Reader;
import io.penguin.penguincore.util.Pair;
import io.penguin.springboot.starter.mapper.ContainerKind;
import io.penguin.springboot.starter.model.MultiBaseOverWriteReaders;
import io.penguin.springboot.starter.model.ReaderBundle;
import lombok.Builder;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class OverWriterFactory implements ReaderFactory<Void> {
    private static final ObjectMapper objectMapper = new ObjectMapper();


    public OverWriterFactory() {
    }

    @Override
    public ContainerKind getContainerType() {
        return ContainerKind.OVER_WRITER;
    }

    @Override
    public Reader<Object, Context<Object>> generate(Void unused, Map<String, Object> spec) throws Exception {
        Map<String, Class<? extends BaseOverWriteReader>> overWriters = objectMapper.convertValue(spec, new TypeReference<>() {
        });

        Map<Class<? extends BaseOverWriteReader>, BaseOverWriteReader> value = overWriters
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


    @Data
    @Builder
    static public class RedisFactoryInput {
        private LettuceConnectionConfig connection;
        private Map<String, Reader<Object, Context<Object>>> readers;

    }
}
