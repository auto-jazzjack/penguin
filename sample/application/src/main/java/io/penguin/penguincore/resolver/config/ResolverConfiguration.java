package io.penguin.penguincore.resolver.config;

import com.example.penguinql.acl.AclProvider;
import com.example.penguinql.acl.providerimpl.GqlAclProvider;
import com.example.penguinql.core.GqlParser;
import com.example.penguinql.core.ResolverService;
import io.penguin.penguincore.http.SampleRequest;
import io.penguin.penguincore.http.SampleResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Objects;

@Configuration
public class ResolverConfiguration {

    @Bean
    public ResolverService<SampleRequest, SampleResponse> resolverService(RootResolver rootResolver, ResolverMapperImpl resolverMapper, AclProvider aclProvider) throws Exception {
        return new ResolverService<>(rootResolver, resolverMapper, aclProvider);
    }

    @Bean
    public AclProvider aclProvider(GqlParser gqlParser) throws Throwable {
        GqlAclProvider gqlAclProvider = new GqlAclProvider(gqlParser);
        ClassPathResource classPathResource = new ClassPathResource("acl");
        File file = classPathResource.getFile();
        Objects.requireNonNull(file);

        for (File i : Objects.requireNonNull(file.listFiles())) {

            FileInputStream fileInputStream = new FileInputStream(i);
            byte[] bytes = new BufferedInputStream(fileInputStream).readAllBytes();
            gqlAclProvider.parseAndPut(new String(bytes));
        }
        return gqlAclProvider;
    }

    @Bean
    public GqlParser gqlParser(RootResolver rootResolver) {
        return new GqlParser(rootResolver.getClass());
    }
}
