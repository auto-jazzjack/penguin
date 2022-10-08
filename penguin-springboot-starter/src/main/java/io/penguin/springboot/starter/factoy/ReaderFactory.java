package io.penguin.springboot.starter.factoy;

import io.penguin.penguincore.reader.CacheContext;
import io.penguin.penguincore.reader.Reader;
import io.penguin.springboot.starter.mapper.ContainerKind;

import java.util.Map;

public interface ReaderFactory {
    ContainerKind getContainerType();

    Reader<Object, CacheContext<Object>> generate(Map<String, Object> spec) throws Exception;

    default Reader<Object, CacheContext<Object>> generateWithReaderPool(
            Map<String, Object> spec,
            Map<String, Reader<Object, CacheContext<Object>>> readers
    ) throws Exception {
        return generate(spec);
    }
}
