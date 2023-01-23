package io.penguin.penguinql.core;

import org.apache.commons.lang3.tuple.Pair;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


public class ExecutionPlanGenerator<T> {

    private final ResolverMeta<T> rootResolver;
    private final ResolverMapper resolverMapper;

    public ExecutionPlanGenerator(ResolverMeta<T> rootResolver, ResolverMapper resolverMapper) {
        this.resolverMapper = resolverMapper;
        this.rootResolver = rootResolver;
        this.rootResolver.decorateResolver(resolverMapper);
    }

    public ExecutionPlan<T> generate(Object request, Query query) {
        ContextQL contextQL = new ContextQL();
        contextQL.setRequest(request);
        return generate(rootResolver, contextQL, query);
    }

    private <M> ExecutionPlan<M> generate(ResolverMeta<M> current, ContextQL context, Query query) {

        if (current == null) {
            return null;
        }

        ExecutionPlan<M> executionPlan = ExecutionPlan.<M>builder()
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


        Map<String, ResolverMeta<?>> next = current.getCurrent().next();
        next.entrySet()
                .stream()
                .filter(i -> collect.contains(i.getKey()))
                .map(i -> Pair.of(i.getKey(), i.getValue()))
                .forEach(i -> {
                    ExecutionPlan generate = generate(
                            new ResolverMeta<>(i.getValue().getCurrentClazz(), i.getValue().getClazz())
                                    .decorateSetter(current.getClazz(), i.getKey())
                                    .decorateResolver(resolverMapper),
                            context, query.getNext().get(i.getKey())
                    );

                    i.getValue()
                            .decorateResolver(resolverMapper)
                            .decorateSetter(current.getClazz(), i.getKey())
                            .getCurrent()
                            .preHandler(context);

                    executionPlan.addNext(i.getKey(), generate);
                });


        return executionPlan;
    }


}
