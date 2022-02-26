package io.penguin.springboot.starter.mapper;

import io.penguin.penguincore.reader.Context;
import io.penguin.penguincore.reader.Reader;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;


public class HelloReader<K, V> implements Reader<String, Context<Map<String, String>>> {

    @Override
    public Mono<Context<Map<String, String>>> findOne(String key) {
        Map<String, String> retv = new HashMap<>();
        retv.put("hello", "hello");
        return Mono.just(Context.<Map<String, String>>builder().value(retv).build());
    }

    @Override
    public Context<Map<String, String>> failFindOne(String key) {
        Map<String, String> retv = new HashMap<>();
        retv.put("fail", "fail");
        return Context.<Map<String, String>>builder().value(retv).build();
    }
}
