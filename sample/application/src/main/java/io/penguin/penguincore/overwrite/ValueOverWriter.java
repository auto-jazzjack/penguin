package io.penguin.penguincore.overwrite;

import io.penguin.penguincore.reader.BaseOverWriteReader;
import reactor.core.publisher.Mono;

import java.util.Map;

public class ValueOverWriter extends BaseOverWriteReader<Long, String, Map<String, String>> {

    public ValueOverWriter() {
    }

    @Override
    public void merge(Map<String, String> agg, String inner) {
        agg.put(inner, inner);
    }

    @Override
    public Mono<String> findOne(Long key) {
        return Mono.just("Overwrite");
    }
}
