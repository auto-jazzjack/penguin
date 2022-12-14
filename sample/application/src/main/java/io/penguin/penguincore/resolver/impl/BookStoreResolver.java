package io.penguin.penguincore.resolver.impl;

import com.example.penguinql.core.DataFetchingEnv;
import com.example.penguinql.core.Resolver;
import com.example.penguinql.core.ResolverMeta;
import com.google.common.collect.ImmutableMap;
import io.penguin.penguincore.http.Book;
import io.penguin.penguincore.http.BookStore;
import io.penguin.penguincore.http.SampleRequest;
import io.penguin.penguincore.model.CBookStore;
import io.penguin.penguincore.reader.BookStoreReader;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BookStoreResolver implements Resolver<List<BookStore>> {

    private final BookStoreReader bookStoreReader;

    @Override
    public Mono<List<BookStore>> generate(DataFetchingEnv condition) {
        List<Long> ids = ((SampleRequest) condition.getContext().getRequest()).getIds();

        return bookStoreReader.findAll(ids)
                .map(i -> {
                    List<Book> collect = Optional.ofNullable(i.getValue()).map(CBookStore::getBook_ids)
                            .orElse(Collections.emptyList())
                            .stream()
                            .map(j -> Book.builder().id(j).build())
                            .collect(Collectors.toList());

                    return BookStore.builder()
                            .id(i.getKey())
                            .books(collect)
                            .contact(Optional.ofNullable(i.getValue()).map(CBookStore::getContact).orElse(null))
                            .build();
                })
                .collectList();
    }

    @Override
    public Map<String, ResolverMeta<?>> next() {
        return ImmutableMap.<String, ResolverMeta<?>>builder()
                .put("books", new ResolverMeta<>(BookResolver.class, Book.class))
                .build();
    }
}
