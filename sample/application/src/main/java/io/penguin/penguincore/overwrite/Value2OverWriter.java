package io.penguin.penguincore.overwrite;

import io.penguin.penguincore.model.MyModel;
import io.penguin.penguincore.reader.BaseOverWriteReader;
import reactor.core.publisher.Mono;

import java.util.Map;

public class Value2OverWriter extends BaseOverWriteReader<Long, String, MyModel> {

    public Value2OverWriter() {
    }

    @Override
    public void merge(MyModel agg, String inner) {
        agg.setFirst(213123142L);
    }

    @Override
    public Mono<String> findOne(Long key) {
        return Mono.just("Overwrite2222");
    }
}
