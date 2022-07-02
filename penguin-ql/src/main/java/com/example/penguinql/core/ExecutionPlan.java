package com.example.penguinql.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExecutionPlan {
    private Resolver<?, Object> mySelf;
    private Map<String, ExecutionPlan> next;
    private Set<String> currFields;
    private Set<String> currObjects;
    private DataFetchingEnv dataFetchingEnv;

    /**
     * This is only helper function generate mySelf
     */
    public Mono<Object> generateMySelf() {
        return this.mySelf.generate(this.dataFetchingEnv);
    }

    public void addNext(String key, ExecutionPlan value) {
        if (next == null) {
            next = new HashMap<>();
        }
        next.put(key, value);
    }
}
