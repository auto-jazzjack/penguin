package com.example.penguinql.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExecutionPlan<Myself> {
    private Resolver<Myself> mySelf;
    private Map<String, ExecutionPlan<Object>> next;
    private Set<String> currFields;
    //private Set<String> currObjects;
    private DataFetchingEnv dataFetchingEnv;

    //Attach myself to parent
    private BiConsumer<Object, Myself> setter;

    /**
     * This is only helper function generate mySelf
     */
    public Mono<Myself> generateMySelf() {
        return this.mySelf.generate(this.dataFetchingEnv);
    }

    public void addNext(String key, ExecutionPlan value) {
        if (next == null) {
            next = new HashMap<>();
        }
        next.put(key, value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExecutionPlan<?> that = (ExecutionPlan<?>) o;
        return Objects.equals(mySelf, that.mySelf);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mySelf);
    }
}
