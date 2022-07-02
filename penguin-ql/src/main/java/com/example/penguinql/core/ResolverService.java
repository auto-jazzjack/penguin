package com.example.penguinql.core;

import reactor.core.publisher.Mono;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Objects;


public class ResolverService<I, O> {

    private final ExecutionPlanGenerator executionPlanGenerator;
    private final ExecutionPlanExecutor executionPlanExecutor;
    private final PojoFieldCleanser pojoFieldCleanser;
    private final GqlParser gqlParser;

    public ResolverService(RootResolver<O> rootResolver, ResolverMapper resolverMapper) throws Exception {
        this.executionPlanGenerator = new ExecutionPlanGenerator(rootResolver, resolverMapper);
        this.executionPlanExecutor = new ExecutionPlanExecutor();
        this.gqlParser = new GqlParser();
        this.pojoFieldCleanser = new PojoFieldCleanser<O>(extractResolverType(rootResolver));
    }

    public Mono<O> exec(I request, String query) {
        ExecutionPlan generate = executionPlanGenerator.generate(request, gqlParser.parseFrom(query));
        return executionPlanExecutor.exec(generate);
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
