package io.penguin.springboot.starter.factoy;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.penguin.penguincore.reader.Context;
import io.penguin.penguincore.reader.Reader;
import io.penguin.springboot.starter.mapper.ContainerKind;
import lombok.Data;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class BeanFactory implements ReaderFactory {
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
    public Reader<Object, Context<Object>> generate(Map<String, Object> spec) {
        Properties<Object, Context<Object>> properties = objectMapper.convertValue(spec, new TypeReference<>() {
        });
        return (Reader<Object, Context<Object>>) applicationContext.getBean(properties.getName(), Reader.class);
    }

    @Data
    static public class Properties<ID, T> {
        private String name;
        private Class<T> domainClass;
        private Class<ID> idClass;
    }

    ;
}
