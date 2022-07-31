package io.penguin.springboot.starter.factoy;

import io.penguin.penguincore.reader.Context;
import io.penguin.penguincore.reader.Reader;
import io.penguin.springboot.starter.mapper.ContainerKind;

import java.util.Map;

public interface ReaderFactory {
    ContainerKind getContainerType();

    Reader<Object, Context<Object>> generate(Map<String, Object> spec) throws Exception;

    default Reader<Object, Context<Object>> generateWithReaderPool(
            Map<String, Object> spec,
            Map<String, Reader<Object, Context<Object>>> readers
    ) throws Exception {
        return generate(spec);
    }
}
