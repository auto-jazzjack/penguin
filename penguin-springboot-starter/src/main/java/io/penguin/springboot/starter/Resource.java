package io.penguin.springboot.starter;

import io.penguin.penguincore.reader.Reader;
import lombok.Data;

import java.util.Map;

@Data
public class Resource {

    private Map<String, Reader> readers;
}
