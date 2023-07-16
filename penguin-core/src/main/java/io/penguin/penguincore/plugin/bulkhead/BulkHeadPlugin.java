package io.penguin.penguincore.plugin.bulkhead;

import io.github.resilience4j.bulkhead.BulkheadFullException;
import io.github.resilience4j.reactor.bulkhead.operator.BulkheadOperator;
import io.micrometer.core.instrument.Counter;
import io.penguin.penguincore.plugin.BulkheadDecorator;
import io.penguin.penguincore.plugin.Plugin;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
public class BulkHeadPlugin<V> implements Plugin<V> {

    private final BulkheadOperator<V> bulkheadOperator;
    private final Counter fail;

    public BulkHeadPlugin(BulkheadDecorator<V> bulkheadDecorator) {
        this.bulkheadOperator = bulkheadDecorator.getBulkheadOperator();
        this.fail = bulkheadDecorator.getFail();
    }


    @Override
    public Mono<V> decorateSource(Mono<V> source) {
        return ((Mono<V>) bulkheadOperator.apply(source))
                .doOnError(i -> {
                    if (i instanceof BulkheadFullException) {
                        fail.increment();
                    }
                });
    }
}
