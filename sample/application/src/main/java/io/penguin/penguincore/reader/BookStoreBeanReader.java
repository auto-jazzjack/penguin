package io.penguin.penguincore.reader;

import io.penguin.penguincore.model.CBookStore;
import io.penguin.springboot.starter.Penguin;
import io.penguin.springboot.starter.flow.From;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class BookStoreBeanReader implements Reader<Long, CBookStore> {

    @Override
    public Mono<CBookStore> findOne(Long key) {

        return null;
    }
}
