package io.penguin.pengiunlettuce.cofig;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum Compression {
    NONE(0),
    DEFLATE(1),
    GZIP(2);

    final Integer compression;


    private static final Map<String, Compression> cached = new HashMap<>();

    static {
        Arrays.stream(Compression.values()).forEach(i -> cached.put(i.name(), i));
    }

    public static Compression defaultOrValueOf(String name) {
        Compression compression = cached.get(name);
        if (compression == null) {
            return NONE;
        } else {
            return compression;
        }
    }
}


