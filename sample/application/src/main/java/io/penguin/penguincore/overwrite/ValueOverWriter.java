package io.penguin.penguincore.overwrite;

import io.penguin.penguincore.model.MyModel;
import io.penguin.penguincore.reader.BaseOverWriteReader;
import reactor.core.publisher.Mono;

import java.util.Map;

public class ValueOverWriter extends BaseOverWriteReader<Long, String, MyModel> {

    public ValueOverWriter() {
    }

    @Override
    public void merge(MyModel agg, String inner) {
        agg.setId(324L);
        //agg.put(inner, inner);
    }

    @Override
    public Mono<String> findOne(Long key) {
        return Mono.just("Overwrite");
    }
}
