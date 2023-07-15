package io.penguin.springboot.starter.factoy;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.penguin.penguincore.reader.CacheContext;
import io.penguin.penguincore.reader.Reader;
import io.penguin.springboot.starter.mapper.ContainerKind;
import lombok.Data;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class BeanFactory<K, V> implements ReaderFactory<K, V> {
    private final ApplicationContext applicationContext;
    private static final ObjectMapper objectMapper = new ObjectMapper();


    public BeanFactory(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public ContainerKind getContainerType() {
        return ContainerKind.BEAN;
    }

    @Override
    public Reader<K, V> generate(Map<String, Object> spec) {
        Properties<Object, CacheContext<Object>> properties = objectMapper.convertValue(spec, new TypeReference<>() {
        });
        return (Reader<K, V>) applicationContext.getBean(properties.getName(), Reader.class);
    }

    @Data
    static public class Properties<ID, T> {
        private String name;
        private Class<T> domainClass;
        private Class<ID> idClass;
    }
}
