package io.penguin.penguincore.resolver.config;

import com.example.penguinql.core.ResolverService;
import io.penguin.penguincore.http.SampleRequest;
import io.penguin.penguincore.http.SampleResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ResolverConfiguration {

    @Bean
    public ResolverService<SampleRequest, SampleResponse> resolverService(RootResolver rootResolver, ResolverMapperImpl resolverMapper) {
        return new ResolverService<>(rootResolver, resolverMapper);
    }
}
