package io.penguin.penguincore.resolver.impl;

import com.example.penguinql.core.ContextQL;
import com.example.penguinql.core.DataFetchingEnv;
import com.example.penguinql.core.Resolver;
import io.penguin.penguincore.http.Book;
import io.penguin.penguincore.http.BookStore;
import io.penguin.penguincore.reader.BookReader;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BookResolver implements Resolver<BookStore, List<Book>> {

    private final BookReader bookReader;

    @Override
    public void setData(BookStore bookStore, List<Book> books) {
        bookStore.setBooks(books);
    }

    @Override
    public void preHandler(ContextQL context) {
    }

    @Override
    public Mono<List<Book>> generate(DataFetchingEnv condition) {
        BookStore nearRoot = (BookStore) condition.getNearRoot().getValueByKey();
        List<Long> collect = Optional.ofNullable(nearRoot.getBooks()).orElse(Collections.emptyList())
                .stream()
                .map(Book::getId)
                .collect(Collectors.toList());
        return bookReader.findAll(collect)
                .map(i -> Book.builder()
                        .id(i.getKey())
                        .price(i.getValue().getPrice())
                        .title(i.getValue().getTitle())
                        .build())
                .collectList();
    }


}
