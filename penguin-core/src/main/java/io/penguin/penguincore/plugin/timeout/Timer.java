package io.penguin.penguincore.plugin.timeout;

import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import org.reactivestreams.Subscription;
import reactor.core.CoreSubscriber;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Timer<V> implements Subscription, CoreSubscriber<V> {

    private final CoreSubscriber<V> source;
    private Subscription subscription;
    private final Timeout timeout;

    public Timer(CoreSubscriber<V> source, HashedWheelTimer timer, long milliseconds) {
        this.source = source;

        timeout = timer.newTimeout(
                timeout -> {
                    throw new TimeoutException();
                }, milliseconds,
                TimeUnit.MILLISECONDS
        );

    }

    @Override
    public void onNext(V v) {
        stopTimer();//Mono case?
        source.onNext(v);
    }

    @Override
    public void onError(Throwable t) {
        stopTimer();
        source.onError(t);
    }

    @Override
    public void onComplete() {
        stopTimer();
        source.onComplete();
    }

    @Override
    public void request(long n) {
        this.subscription.request(n);

    }

    @Override
    public void cancel() {
        stopTimer();
        this.subscription.cancel();
    }

    @Override
    public void onSubscribe(Subscription s) {
        this.subscription = s;
        source.onSubscribe(this);

    }

    void stopTimer(){
        if (!timeout.isCancelled()) {
            timeout.cancel();
        }
    }
}
