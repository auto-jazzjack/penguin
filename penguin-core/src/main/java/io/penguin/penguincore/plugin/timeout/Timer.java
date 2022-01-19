package io.penguin.penguincore.plugin.timeout;

import io.micrometer.core.instrument.Counter;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.penguin.penguincore.exception.TimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscription;
import reactor.core.CoreSubscriber;

import java.util.concurrent.TimeUnit;

@Slf4j
public class Timer<V> implements Subscription, CoreSubscriber<V> {

    private final CoreSubscriber<V> source;
    private Subscription subscription;
    private final Timeout timeout;
    private final Counter counter;

    public Timer(CoreSubscriber<V> source, Counter counter, HashedWheelTimer timer, long milliseconds) {
        this.source = source;
        timeout = timer.newTimeout(
                timeout -> onError(new TimeoutException()), milliseconds,
                TimeUnit.MILLISECONDS
        );
        this.counter = counter;
    }

    @Override
    public void onNext(V v) {
        if (!timeout.isCancelled()) {
            timeout.cancel();
        }

        source.onNext(v);
    }

    @Override
    public void onError(Throwable t) {
        if (!timeout.isCancelled()) {
            timeout.cancel();
        }

        source.onError(t);

        if (t instanceof TimeoutException) {
            counter.increment();
        }
    }

    @Override
    public void onComplete() {
        if (!timeout.isCancelled()) {
            timeout.cancel();
        }

        source.onComplete();
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

    public void stopTimerWhenExpired() {
        if (timeout.isExpired()) {
            throw new TimeoutException();
        }
    }
}
