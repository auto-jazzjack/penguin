package com.example.penguinql.core;

import reactor.core.publisher.Mono;


public class ResolverService<I, O> {

    private final ExecutionPlanGenerator executionPlanGenerator;
    private final ExecutionPlanExecutor executionPlanExecutor;

    public ResolverService(RootResolver<O> rootResolver, ResolverMapper resolverMapper) {
        this.executionPlanGenerator = new ExecutionPlanGenerator(rootResolver, resolverMapper);
        this.executionPlanExecutor = new ExecutionPlanExecutor();
    }

    public Mono<O> exec(I request, String query) {
        ExecutionPlan generate = executionPlanGenerator.generate(GqlParser.parseFrom(query));
        return executionPlanExecutor.exec(generate);
    }
}
