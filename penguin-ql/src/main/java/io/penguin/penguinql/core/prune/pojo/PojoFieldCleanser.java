package io.penguin.penguinql.core.prune.pojo;


import io.penguin.penguinql.core.ExecutionPlan;
import io.penguin.penguinql.core.prune.FieldMeta;
import io.penguin.penguinql.core.prune.GenericType;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Slf4j
public class PojoFieldCleanser<T> {

    private final PojoField<T> fieldCleanerMeta;

    public PojoFieldCleanser(Class<T> root) throws Exception {
        fieldCleanerMeta = new PojoField<>(root, GenericType.NONE);
        System.out.println();
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
                .forEach(i -> {
                    FieldMeta<T1> fieldCleanerMeta = (FieldMeta<T1>) fieldMeta.getLeafChildren().get(i);
                    fieldCleanerMeta.setData(retv, fieldCleanerMeta.getData(result));
                });

        Optional.ofNullable(executionPlan.getNext()).map(Map::keySet).orElse(Collections.emptySet())
                .stream()
                .filter(i -> fieldMeta.getExtendableChildren().containsKey(i))
                .forEach(i -> {

                    FieldMeta<Object> nextFieldMeta = fieldMeta.getExtendableChildren().get(i);
                    ExecutionPlan<Object> next = executionPlan.getNext().get(i);
                    switch (nextFieldMeta.getGenericType()) {
                        case MAP:
                        case NONE:
                            T1 res = exec0((T1) nextFieldMeta.getData(result), (FieldMeta<T1>) nextFieldMeta, (ExecutionPlan<T1>) next);
                            nextFieldMeta.setData(retv, res);
                            break;
                        case LIST:
                            List<T1> collect = ((List<Object>) nextFieldMeta.getData(result))
                                    .stream()
                                    .map(j -> exec0((T1) j, (FieldMeta<T1>) nextFieldMeta, (ExecutionPlan<T1>) next))
                                    .collect(Collectors.toList());
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
