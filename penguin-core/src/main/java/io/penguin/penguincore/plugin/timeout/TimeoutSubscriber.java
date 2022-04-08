package io.penguin.penguincore.plugin.timeout;

import io.micrometer.core.instrument.Counter;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.penguin.penguincore.exception.TimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscription;
import reactor.core.CoreSubscriber;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class TimeoutSubscriber<V> implements Subscription, CoreSubscriber<V> {

    private final CoreSubscriber<V> source;
    private Subscription subscription;
    private final Timeout timeout;
    private final Counter counter;
    private final AtomicBoolean next;
    private final AtomicBoolean completed;

    public TimeoutSubscriber(CoreSubscriber<V> source, Counter counter, HashedWheelTimer timer, long milliseconds) {
        this.source = source;
        timeout = timer.newTimeout(
                timeout -> onError(new TimeoutException()), milliseconds,
                TimeUnit.MILLISECONDS
        );
        this.next = new AtomicBoolean(false);
        this.completed = new AtomicBoolean(false);
        this.counter = counter;
    }

    @Override
    public void onNext(V v) {
        if (next.compareAndSet(false, true)) {

            if (!timeout.isCancelled()) {
                timeout.cancel();
            }
            source.onNext(v);
        }
    }

    @Override
    public void onError(Throwable t) {

        if (!completed.get() && !next.get()) {
            if (!timeout.isCancelled()) {
                timeout.cancel();
            }
            source.onError(t);
        }
    }

    @Override
    public void onComplete() {
        if (next.get() && completed.compareAndSet(false, true)) {
            if (!timeout.isCancelled()) {
                timeout.cancel();
            }
            counter.increment();
            source.onComplete();
        }
    }

    @Override
    public void request(long n) {
        this.subscription.request(n);

    }

    @Override
    public void cancel() {
        if (!timeout.isCancelled()) {
            timeout.cancel();
        }

        this.subscription.cancel();
    }

    @Override
    public void onSubscribe(Subscription s) {
        this.subscription = s;
        source.onSubscribe(this);
    }
}