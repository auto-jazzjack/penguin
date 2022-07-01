package io.penguin.penguincore.resolver.config;

import com.example.penguinql.core.Resolver;
import com.google.common.collect.ImmutableMap;
import io.penguin.penguincore.http.SampleResponse;
import io.penguin.penguincore.resolver.impl.BookStoreResolver;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class RootResolver implements com.example.penguinql.core.RootResolver<SampleResponse> {

    @Override
    public Map<String, Class<? extends Resolver>> next() {
        return ImmutableMap.<String, Class<? extends Resolver>>builder()
                .put("bookStores", BookStoreResolver.class)
                .build();
    }
}
