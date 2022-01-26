package io.penguin.penguincore.plugin.bulkhead;

import io.github.resilience4j.bulkhead.BulkheadFullException;
import io.github.resilience4j.reactor.bulkhead.operator.BulkheadOperator;
import io.micrometer.core.instrument.Counter;
import io.penguin.penguincore.plugin.Ingredient.BulkheadIngredient;
import io.penguin.penguincore.plugin.Plugin;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
public class BulkheadPlugin<V> extends Plugin<V> {

    private final Counter success;
    private final BulkheadOperator<V> bulkheadOperator;
    private final Counter fail;

    public BulkheadPlugin(BulkheadIngredient bulkheadIngredient) {
        super();
        this.bulkheadOperator = (BulkheadOperator<V>) bulkheadIngredient.getBulkheadOperator();
        this.success = bulkheadIngredient.getSuccess();
        this.fail = bulkheadIngredient.getFail();
    }

    @Override
    public Mono<V> decorateSource(Mono<V> source) {
        this.source = (Mono<V>) bulkheadOperator.apply(source);
        return this.source
                .doOnError(i -> {
                    if (i instanceof BulkheadFullException) {
                        fail.increment();
                    }
                })
                .doOnSuccess(i -> success.increment());
    }
}
