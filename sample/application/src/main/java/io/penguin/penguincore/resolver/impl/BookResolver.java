package io.penguin.penguincore.resolver.impl;

import com.example.penguinql.core.ContextQL;
import com.example.penguinql.core.DataFetchingEnv;
import com.example.penguinql.core.Resolver;
import io.penguin.penguincore.model.Book;
import io.penguin.penguincore.model.BookStore;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BookResolver implements Resolver<BookStore, List<Book>> {

    private final BookStoreResolver bookStoreResolver;

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

        return Mono.just(Stream.of(
                        Book.builder()
                                .title(nearRoot.getContact())
                                .price(80000L)
                                .build(),
                        Book.builder()
                                .title(nearRoot.getContact())
                                .price(50000L)
                                .build()
                )
                .collect(Collectors.toList()));
    }


}
