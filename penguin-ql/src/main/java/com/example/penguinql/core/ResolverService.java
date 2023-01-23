package com.example.penguinql.core;

import com.example.penguinql.acl.AclAuth;
import com.example.penguinql.acl.AclProvider;
import com.example.penguinql.core.prune.pojo.PojoFieldCleanser;
import com.example.penguinql.exception.InvalidQueryException;
import com.example.penguinql.exception.NotAuthorizationException;
import reactor.core.publisher.Mono;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Objects;


public class ResolverService<I, O> {

    private final ExecutionPlanGenerator<O> executionPlanGenerator;
    private final ExecutionPlanExecutor executionPlanExecutor;
    private final PojoFieldCleanser<O> pojoFieldCleanser;
    private final GqlParser gqlParser;
    private final AclProvider aclProvider;

    public ResolverService(RootResolver<O> rootResolver, ResolverMapper resolverMapper, AclProvider aclProvider) throws Exception {
        this.executionPlanGenerator = new ExecutionPlanGenerator<>(new ResolverMeta<>((Class<? extends Resolver<O>>) rootResolver.getClass(), rootResolver.clazz()), resolverMapper);
        this.executionPlanExecutor = new ExecutionPlanExecutor();
        this.gqlParser = new GqlParser(rootResolver.clazz());
        this.pojoFieldCleanser = new PojoFieldCleanser<>(extractResolverType(rootResolver));
        this.aclProvider = aclProvider;
    }

    public Mono<O> exec(I request, String consumer, String query) {
        try {
            Query parsedQuery = gqlParser.parseFrom(query);
            AclAuth.contains(aclProvider.getAclAuth(consumer), parsedQuery);
            ExecutionPlan<O> generate = executionPlanGenerator.generate(request, parsedQuery);
            return executionPlanExecutor.exec(generate)
                    .map(i -> pojoFieldCleanser.exec(i, generate));
        } catch (InvalidQueryException | NotAuthorizationException e) {
            return Mono.error(e);
        }


    }

    private Class<O> extractResolverType(RootResolver<O> rootResolver) {
        ParameterizedType type = Arrays.stream(rootResolver.getClass().getGenericInterfaces())
                .filter(i -> {
                    if (i instanceof ParameterizedType) {
                        return ((ParameterizedType) i).getRawType().equals(RootResolver.class);
                    } else {
                        return false;
                    }
                })
                .map(i -> (ParameterizedType) i)
                .findFirst().orElse(null);

        Objects.requireNonNull(type);
        if (type.getActualTypeArguments().length != 1) {
            throw new IllegalStateException("Should not be reached");
        }

        Type actualTypeArgument = type.getActualTypeArguments()[0];
        return (Class<O>) actualTypeArgument;

    }
}
