package com.example.penguinql.core;

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

    public ResolverService(RootResolver<O> rootResolver, ResolverMapper resolverMapper) throws Exception {
        this.executionPlanGenerator = new ExecutionPlanGenerator<>(new ResolverMeta<O>((Class<? extends Resolver<O>>) rootResolver.getClass(), rootResolver.clazz()), resolverMapper);
        this.executionPlanExecutor = new ExecutionPlanExecutor();
        this.gqlParser = new GqlParser(rootResolver.clazz());
        this.pojoFieldCleanser = new PojoFieldCleanser<>(extractResolverType(rootResolver));
    }

    public Mono<O> exec(I request, String query) {
        ExecutionPlan<O> generate = executionPlanGenerator.generate(request, gqlParser.parseFrom(query));
        return executionPlanExecutor.exec(generate)
                .map(i -> pojoFieldCleanser.exec(i, generate));
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
