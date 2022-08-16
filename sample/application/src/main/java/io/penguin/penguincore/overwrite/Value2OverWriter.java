package io.penguin.penguincore.overwrite;

import io.penguin.penguincore.model.CBookStore;
import io.penguin.penguincore.reader.BaseOverWriteReader;
import reactor.core.publisher.Mono;

public class Value2OverWriter extends BaseOverWriteReader<Long, String, CBookStore> {

    public Value2OverWriter() {
    }

    @Override
    public void merge(CBookStore agg, String inner) {
        agg.setContact("213123142L");
    }

    @Override
    public Mono<String> findOne(Long key) {
        return Mono.just("Overwrite2222");
    }
}
