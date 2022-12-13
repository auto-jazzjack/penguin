package io.penguin.penguincore.resolver.config;

import com.example.penguinql.acl.AclProvider;
import com.example.penguinql.acl.providerimpl.GqlAclProvider;
import com.example.penguinql.core.GqlParser;
import com.example.penguinql.core.ResolverService;
import io.penguin.penguincore.http.SampleRequest;
import io.penguin.penguincore.http.SampleResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ResolverConfiguration {

    @Bean
    public ResolverService<SampleRequest, SampleResponse> resolverService(RootResolver rootResolver, ResolverMapperImpl resolverMapper) throws Exception {
        return new ResolverService<>(rootResolver, resolverMapper);
    }

    @Bean
    public AclProvider aclProvider(GqlParser gqlParser) {
        return new GqlAclProvider(gqlParser);
    }

    @Bean
    public GqlParser gqlParser(RootResolver rootResolver) {
        return new GqlParser(rootResolver.getClass());
    }
}
