package io.penguin.penguincore.reader;


import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Map;

public class Source implements Reader<String, Map<String,String>> {

    @Override
    public Mono<Map<String, String>> findOne(String key) {

        return Mono.just(Collections.emptyMap());
    }
}
