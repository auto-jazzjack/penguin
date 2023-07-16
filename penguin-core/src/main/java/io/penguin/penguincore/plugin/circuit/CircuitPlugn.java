package io.penguin.penguincore.plugin.circuit;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import io.micrometer.core.instrument.Counter;
import io.penguin.penguincore.plugin.CircuitDecorator;
import io.penguin.penguincore.plugin.Plugin;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscription;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoOperator;

public class CircuitPlugn<V> extends MonoOperator<V, V> {

    private final CircuitBreakerOperator<V> circuitBreakerOperator;
    private final Counter success;
    private final Counter fail;

    public CircuitPlugn(Mono<V> src, CircuitDecorator<V> circuitDecorator) {
        super(src);
        this.circuitBreakerOperator = circuitDecorator.getCircuitBreakerOperator();
        this.success = circuitDecorator.getSuccess();
        this.fail = circuitDecorator.getFail();
    }

    @Override
    public void subscribe(CoreSubscriber<? super V> actual) {
        this.circuitBreakerOperator.apply((Publisher<V>) this.source).subscribe(new CoreSubscriber<V>() {
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
                if (t instanceof CallNotPermittedException) {
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
