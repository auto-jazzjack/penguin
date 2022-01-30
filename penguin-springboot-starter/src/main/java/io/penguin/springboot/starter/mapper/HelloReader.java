package io.penguin.springboot.starter.mapper;

import io.penguin.penguincore.reader.Reader;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;


public class HelloReader implements Reader<String, Map<String, String>> {
    @Override
    public Mono<Map<String, String>> findOne(String key) {
        Map<String, String> retv = new HashMap<>();
        retv.put("hello", "hello");
        return Mono.just(retv);
    }

    @Override
    public Map<String, String> failFindOne(String key) {
        Map<String, String> retv = new HashMap<>();
        retv.put("fail", "fail");
        return retv;
    }
}
