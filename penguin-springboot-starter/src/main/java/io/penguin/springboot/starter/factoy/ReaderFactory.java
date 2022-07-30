package io.penguin.springboot.starter.factoy;

import io.penguin.penguincore.reader.Context;
import io.penguin.penguincore.reader.Reader;
import io.penguin.springboot.starter.mapper.ContainerKind;

import java.util.Map;

public interface ReaderFactory {
    ContainerKind getContainerType();

    Reader<Object, Context<Object>> generate(Map<String, Object> spec);
}
