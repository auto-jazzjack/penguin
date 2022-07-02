package com.example.penguinql.core;


import com.example.penguinql.util.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Input) execution plan
 * Output)
 */
public class ExecutionPlanExecutor {

    public <T> Mono<T> exec(ExecutionPlan executionPlan) {
        ExecutionPlan ignoreRoot = executionPlan.getNext().entrySet().stream()
                .findFirst()
                .map(Map.Entry::getValue)
                .orElse(null);

        return exec0(executionPlan);
    }

    public <T> Mono<T> exec0(ExecutionPlan executionPlan) {
        if (executionPlan == null) {
            return Mono.empty();
        }

        if (CollectionUtils.isEmpty(executionPlan.getNext())) {
            return (Mono<T>) executionPlan.generateMySelf();
        }

        Mono<Object> generate = executionPlan.generateMySelf().cache();

        Mono<Map<Object, Pair<Resolver, Object>>> collect = generate.flatMapMany(i -> {
            if (i instanceof List) {
                List<Object> result = (List<Object>) i;
                Flux<Triple<Resolver, Object, Object>> tripleFlux = Flux.empty();
                for (int k = 0; k < result.size(); k++) {
                    final int k1 = k;
                    tripleFlux = tripleFlux.concatWith(Flux.fromIterable(executionPlan.getNext().entrySet())
                            .flatMap(j -> {
                                j.getValue().getDataFetchingEnv()
                                        .setRoot(executionPlan.getDataFetchingEnv().getRoot())
                                        .setNearRoot(KeyValue.of(k1, result.get(k1)));
                                return this.exec0(j.getValue()).map(l -> Triple.of(j.getValue().getMySelf(), k1, l));
                            }));
                }
                return tripleFlux;

            } else if (i instanceof Map) {
                Map<Object, Object> result = (Map<Object, Object>) i;
                Flux<Triple<Resolver, Object, Object>> tripleFlux = Flux.empty();
                for (Map.Entry<Object, Object> entry : result.entrySet()) {
                    final Object k1 = entry.getKey();
                    tripleFlux = tripleFlux.concatWith(Flux.fromIterable(executionPlan.getNext().entrySet())
                            .flatMap(j -> {
                                j.getValue().getDataFetchingEnv()
                                        .setRoot(executionPlan.getDataFetchingEnv().getRoot())
                                        .setNearRoot(KeyValue.of(entry.getKey(), result.get(k1)));
                                return this.exec0(j.getValue()).map(l -> Triple.of(j.getValue().getMySelf(), k1, l));
                            }));
                }
                return tripleFlux;
            } else {
                return Flux.fromIterable(executionPlan.getNext().entrySet())
                        .flatMap(j -> {
                            ExecutionPlan value = j.getValue();
                            value.getDataFetchingEnv()
                                    .setRoot(executionPlan.getDataFetchingEnv().getRoot())
                                    .setNearRoot(executionPlan.getDataFetchingEnv().getNearRoot());
                            return this.exec0(value).map(l -> Triple.of(j.getValue().getMySelf(), null, l));
                        });
            }
        }).collect(Collectors.toMap(Triple::getMiddle, i -> Pair.of(i.getLeft(), i.getRight())));

        return Mono.zip(generate, collect)
                .map(i -> {

                    if (i.getT1() instanceof List) {
                        List<Object> t = (List<Object>) i.getT1();
                        i.getT2().forEach((key, value) -> {
                            int idx = (Integer) key;
                            value.getKey().setData(t.get(idx), value.getValue());
                        });
                    } else {
                        i.getT2().forEach((key, value) -> value.getKey().setData(i.getT1(), value.getValue()));
                    }

                    return (T) i.getT1();
                });

    }
}
