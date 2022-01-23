package io.penguin.penguincore.plugin.bulkhead;

import io.github.resilience4j.bulkhead.BulkheadFullException;
import io.github.resilience4j.reactor.bulkhead.operator.BulkheadOperator;
import io.micrometer.core.instrument.Counter;
import io.penguin.penguincore.plugin.Ingredient.AllIngredient;
import io.penguin.penguincore.plugin.Plugin;
import lombok.extern.slf4j.Slf4j;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.Mono;

@Slf4j
public class BulkheadPlugin<V> extends Plugin<V> {

    private final Counter success;
    private final Counter fail;

    public BulkheadPlugin(Mono<V> source, AllIngredient allIngredient) {
        super(source, allIngredient);
        BulkheadOperator<V> bulkheadOperator = (BulkheadOperator<V>) allIngredient.getBulkheadIngredient().getBulkheadOperator();
        this.source = (Mono<V>) bulkheadOperator.apply(source);
        this.success = allIngredient.getBulkheadIngredient().getSuccess();
        this.fail = allIngredient.getBulkheadIngredient().getFail();
    }


    @Override
    public void subscribe(CoreSubscriber<? super V> actual) {
        source.doOnError(i -> {
                    if (i instanceof BulkheadFullException) {
                        fail.increment();
                    }
                })
                .doOnSuccess(i -> success.increment())
                .subscribe(actual);
    }
}
