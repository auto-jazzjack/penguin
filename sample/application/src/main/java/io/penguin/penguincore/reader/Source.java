package io.penguin.penguincore.reader;


import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.HashMap;

import java.util.Map;

public class Source implements Reader<String, Map<String,String>> {

    @Override
    public Mono<Map<String, String>> findOne(String key) {

        Map<String, String> retv = new HashMap<>();

        retv.put("hello0", "hello0");
        retv.put("hello1", "hello1");
        retv.put("hello2", "hello2");

        return Mono.just(retv);
    }
}
