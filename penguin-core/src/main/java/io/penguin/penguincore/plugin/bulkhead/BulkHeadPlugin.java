package io.penguin.penguincore.plugin.bulkhead;

import io.github.resilience4j.bulkhead.BulkheadFullException;
import io.github.resilience4j.reactor.bulkhead.operator.BulkheadOperator;
import io.micrometer.core.instrument.Counter;
import io.penguin.penguincore.plugin.BulkheadDecorator;
import io.penguin.penguincore.plugin.Plugin;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscription;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoOperator;

@Slf4j
public class BulkHeadPlugin<V> extends MonoOperator<V, V> {

    private final BulkheadOperator<V> bulkheadOperator;
    private final Counter fail;
    private Counter success;

    public BulkHeadPlugin(Mono<V> src, BulkheadDecorator<V> bulkheadDecorator) {
        super(src);
        this.bulkheadOperator = bulkheadDecorator.getBulkheadOperator();
        this.fail = bulkheadDecorator.getFail();
        this.success = bulkheadDecorator.getSuccess();
    }

    @Override
    public void subscribe(CoreSubscriber<? super V> actual) {
        this.source.subscribe(new CoreSubscriber<V>() {
            @Override
            public void onSubscribe(Subscription s) {
                actual.onSubscribe(s);
            }

            @Override
            public void onNext(V v) {
                success.increment();
                actual.onNext(v);
            }

            @Override
            public void onError(Throwable t) {
                if (t instanceof BulkheadFullException) {
                    fail.increment();
                }
                actual.onError(t);
            }

            @Override
            public void onComplete() {
                actual.onComplete();
            }
        });
    }

}
