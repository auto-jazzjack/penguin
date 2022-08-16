package io.penguin.springboot.starter.config;

import io.penguin.penguincore.reader.Reader;
import lombok.Data;

import java.util.Map;

@Data
public class Resource {
    private Map<String, Reader<?, ?>> readers;
}
