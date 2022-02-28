package io.penguin.springboot.starter.util;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class IdTypeDetermineUtil {

    private final static Map<String, Function<String, ?>> idTypeConverter;

    static {
        idTypeConverter = new HashMap<>();
        idTypeConverter.put("LONG", Long::parseLong);
        idTypeConverter.put("INTEGER", Integer::parseInt);
        idTypeConverter.put("STRING", s -> s);
    }

    public static Function<String, ?> getConverter(String idType) {
        return idTypeConverter.getOrDefault(idType.toUpperCase(), (Function<String, String>) s -> s);
    }
}
