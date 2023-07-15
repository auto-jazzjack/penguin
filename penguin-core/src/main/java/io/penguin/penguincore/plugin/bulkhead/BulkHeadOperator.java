package io.penguin.penguincore.plugin.bulkhead;

import io.github.resilience4j.bulkhead.BulkheadFullException;
import io.github.resilience4j.reactor.bulkhead.operator.BulkheadOperator;
import io.micrometer.core.instrument.Counter;
import io.penguin.penguincore.plugin.BulkheadDecorator;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoOperator;

@Slf4j
public class BulkHeadOperator<V> extends MonoOperator<V, V> {

    private final Counter success;
    private final BulkheadOperator<V> bulkheadOperator;
    private final Counter fail;

    public BulkHeadOperator(Mono<? extends V> source, BulkheadDecorator bulkheadDecorator) {
        super(source);
        this.bulkheadOperator = (BulkheadOperator<V>) bulkheadDecorator.getBulkheadOperator();
        this.success = bulkheadDecorator.getSuccess();
        this.fail = bulkheadDecorator.getFail();
    }


    @Override
    public void subscribe(CoreSubscriber<? super V> actual) {
        this.source.subscribe(
                        actual
                                .doOnError(i -> {
                                    if (i instanceof BulkheadFullException) {
                                        fail.increment();
                                    }
                                })
                                .doOnSuccess(i -> success.increment())
                )
                ;
    }
}
