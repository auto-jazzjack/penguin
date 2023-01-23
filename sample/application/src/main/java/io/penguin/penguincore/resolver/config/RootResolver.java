package io.penguin.penguincore.resolver.config;

import io.penguin.penguinql.core.DataFetchingEnv;
import io.penguin.penguinql.core.ResolverMeta;
import com.google.common.collect.ImmutableMap;
import io.penguin.penguincore.http.BookStore;
import io.penguin.penguincore.http.SampleResponse;
import io.penguin.penguincore.resolver.impl.BookStoreResolver;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class RootResolver implements io.penguin.penguinql.core.RootResolver<SampleResponse> {

    @Override
    public Mono<SampleResponse> generate(DataFetchingEnv condition) {
        return Mono.just(new SampleResponse());
    }

    @Override
    public Map<String, ResolverMeta<?>> next() {
        return ImmutableMap.<String, ResolverMeta<?>>builder()
                .put("bookStores", new ResolverMeta<>(BookStoreResolver.class, BookStore.class))
                .build();
    }

    @Override
    public Class<SampleResponse> clazz() {
        return SampleResponse.class;
    }
}
