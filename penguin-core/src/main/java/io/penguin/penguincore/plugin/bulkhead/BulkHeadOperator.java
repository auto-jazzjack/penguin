package io.penguin.penguincore.plugin.bulkhead;

import io.github.resilience4j.bulkhead.BulkheadFullException;
import io.micrometer.core.instrument.Counter;
import io.penguin.penguincore.plugin.BulkheadDecorator;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscription;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoOperator;

@Slf4j
public class BulkHeadOperator<V> extends MonoOperator<V, V> {

    private final Counter fail;
    private final Counter success;

    public BulkHeadOperator(Mono<V> src, BulkheadDecorator<V> bulkheadDecorator) {
        super((Mono<? extends V>) bulkheadDecorator.getBulkheadOperator().apply(src));
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
