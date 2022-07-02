package com.example.penguinql.core;

import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.stream.Collectors;


public class ExecutionPlanGenerator {

    private final Resolver rootResolver;
    private final ResolverMapper resolverMapper;

    public ExecutionPlanGenerator(Resolver rootResolver, ResolverMapper resolverMapper) {
        this.rootResolver = rootResolver;
        this.resolverMapper = resolverMapper;
    }

    public ExecutionPlan generate(Object request, Query query) {
        ContextQL contextQL = new ContextQL();
        contextQL.setRequest(request);
        return generate(rootResolver, contextQL, query);
    }

    private ExecutionPlan generate(Resolver current, ContextQL context, Query query) {

        if (current == null) {
            return null;
        }
        ExecutionPlan executionPlan = ExecutionPlan.builder()
                .mySelf(current)
                .currFields(query.getFields())
                .currObjects(query.getQueryByResolverName().keySet())
                .dataFetchingEnv(new DataFetchingEnv().setContext(context))
                .build();

        //Query resolver에서 value가 not null인 케이스를 돈다
        Set<String> collect = Optional.ofNullable(query.getQueryByResolverName())
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
                    ExecutionPlan generate = generate(i.getValue(), context, query.getQueryByResolverName().get(i.getKey()));
                    i.getValue().preHandler(context);

                    executionPlan.addNext(i.getKey(), generate);
                });


        return executionPlan;
    }
}
