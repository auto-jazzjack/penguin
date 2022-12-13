package com.example.penguinql.core;

import org.apache.commons.lang3.tuple.Pair;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


public class ExecutionPlanGenerator<T> {

    private final Resolver<T> rootResolver;
    private final ResolverMapper resolverMapper;

    public ExecutionPlanGenerator(Resolver<T> rootResolver, ResolverMapper resolverMapper) {
        this.rootResolver = rootResolver;
        this.resolverMapper = resolverMapper;
    }

    public ExecutionPlan<Void, T> generate(Object request, Query query) {
        ContextQL contextQL = new ContextQL();
        contextQL.setRequest(request);
        return generate(rootResolver, contextQL, query);
    }

    private <P, M> ExecutionPlan<P, M> generate(Resolver<M> current, ContextQL context, Query query) {

        if (current == null) {
            return null;
        }

        ExecutionPlan<P, M> executionPlan = ExecutionPlan.<P, M>builder()
                .mySelf(current)
                .currFields(query.getCurrent())
                .dataFetchingEnv(new DataFetchingEnv().setContext(context))
                .build();

        //Query resolver에서 value가 not null인 케이스를 돈다
        Set<String> collect = Optional.ofNullable(query.getNext())
                .orElse(Collections.emptyMap())
                .entrySet()
                .stream()
                .filter(i -> i.getValue() != null)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());


        Map<String, Class<? extends Resolver>> next = current.next();
        next.entrySet()
                .stream()
                .filter(i -> collect.contains(i.getKey()))
                .map(i -> Pair.of(i.getKey(), resolverMapper.toInstant(i.getValue())))
                .forEach(i -> {
                    ExecutionPlan<P, M> generate = generate(i.getValue(), context, query.getNext().get(i.getKey()));
                    i.getValue().preHandler(context);

                    executionPlan.addNext(i.getKey(), generate);
                });


        return executionPlan;
    }
}
