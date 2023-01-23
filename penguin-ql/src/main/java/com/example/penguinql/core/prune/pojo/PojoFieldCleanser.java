package com.example.penguinql.core.prune.pojo;


import com.example.penguinql.core.ExecutionPlan;
import com.example.penguinql.core.prune.FieldMeta;
import com.example.penguinql.core.prune.GenericType;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.penguinql.core.prune.FieldMeta.VALUE;
import static com.example.penguinql.core.prune.GenericType.*;


@Slf4j
public class PojoFieldCleanser<T> {

    private final PojoField<T> fieldCleanerMeta;

    public PojoFieldCleanser(Class<T> root) throws Exception {
        fieldCleanerMeta = new PojoField<>(root, NONE);
    }

    public T exec(T result, ExecutionPlan<T> executionPlan) {
        try {
            return exec0(result, this.fieldCleanerMeta, executionPlan);
        } catch (Exception e) {
            log.error("");
            throw new RuntimeException(e);
        }
    }

    private <T1> T1 exec0(T1 result, FieldMeta<T1> fieldMeta, ExecutionPlan<T1> executionPlan) {
        T1 retv = fieldMeta.baseInstance();

        Optional.ofNullable(executionPlan.getCurrFields()).orElse(Collections.emptySet())
                .stream()
                .filter(i -> fieldMeta.getLeafChildren().containsKey(i))
                .forEach(i -> {
                    FieldMeta<T1> fieldCleanerMeta = (FieldMeta<T1>) fieldMeta.getLeafChildren().get(i);
                    fieldCleanerMeta.setData(retv, fieldCleanerMeta.getData(result));
                });

        Optional.ofNullable(executionPlan.getNext()).map(Map::keySet).orElse(Collections.emptySet())
                .stream()
                .filter(i -> fieldMeta.getExtendableChildren().containsKey(i))
                .forEach(i -> {
                    FieldMeta nextFieldMeta = fieldMeta.getExtendableChildren().get(i);
                    ExecutionPlan<Object> next = executionPlan.getNext().get(i);
                    switch (nextFieldMeta.getGenericType()) {
                        case MAP:
                        case NONE:
                            T1 res = (T1) exec0((T1) nextFieldMeta.getData(result), nextFieldMeta, (ExecutionPlan<T1>) next);
                            nextFieldMeta.setData(retv, res);
                            break;
                        case LIST:
                            FieldMeta<T1> listValue = (FieldMeta<T1>) nextFieldMeta.getExtendableChildren().get(VALUE);

                            List<T1> collect = ((List<Object>) nextFieldMeta.getData(result))
                                    .stream()
                                    .map(j -> exec0((T1) j, listValue, (ExecutionPlan<T1>) next))
                                    .collect(Collectors.toList())
                                    ;
                            nextFieldMeta.setData(retv, collect);
                            break;
                        case SET:
                        default:
                            throw new RuntimeException("Should not be reached");
                    }


                });

        return retv;
    }




}
