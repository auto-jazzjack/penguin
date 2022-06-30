package io.penguin.penguincore.resolver.impl;

import com.example.penguinql.core.DataFetchingEnv;
import com.example.penguinql.core.Resolver;
import com.google.common.collect.ImmutableMap;
import io.penguin.penguincore.http.SampleRequest;
import io.penguin.penguincore.http.SampleResponse;
import io.penguin.penguincore.model.BookStore;
import io.penguin.penguincore.reader.BookStoreReader;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BookStoreResolver implements Resolver<SampleResponse, List<BookStore>> {

    private final BookStoreReader bookStoreReader;

    @Override
    public void setData(SampleResponse sampleResponse, List<BookStore> myself) {
        sampleResponse.setBookStores(myself);
    }

    @Override
    public Mono<List<BookStore>> generate(DataFetchingEnv condition) {
        List<Long> ids = ((SampleRequest) condition.getContext().getRequest()).getIds();

        return bookStoreReader.findAll(ids)
                .map(i -> new ArrayList<>(i.values()));
        /*return Mono.just(Stream.of(BookStore.builder()
                        .contact("123457")
                        .build(),
                BookStore.builder()
                        .contact("123456")
                        .build()
        ).collect(Collectors.toList()));*/
    }

    @Override
    public Map<String, Class<? extends Resolver>> next() {
        return ImmutableMap.<String, Class<? extends Resolver>>builder()
                .put("books", BookResolver.class)
                .build();
    }
}
