package com.example.penguinql.core;

import reactor.core.publisher.Mono;


public class ResolverService<I, O> {

    private final ExecutionPlanGenerator executionPlanGenerator;
    private final ExecutionPlanExecutor executionPlanExecutor;
    private final GqlParser gqlParser;

    public ResolverService(RootResolver<O> rootResolver, ResolverMapper resolverMapper) {
        this.executionPlanGenerator = new ExecutionPlanGenerator(rootResolver, resolverMapper);
        this.executionPlanExecutor = new ExecutionPlanExecutor();
        this.gqlParser = new GqlParser();
    }

    public Mono<O> exec(I request, String query) {
        ExecutionPlan generate = executionPlanGenerator.generate(request, gqlParser.parseFrom(query));
        return executionPlanExecutor.exec(generate);
    }
}
