package io.penguin.penguincore.plugin.timeout;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import io.netty.util.HashedWheelTimer;
import io.penguin.penguincore.plugin.Plugin;
import io.penguin.penguincore.plugin.PluginInput;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscription;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Objects;
import java.util.Optional;

public class Timer<V> implements Subscription, CoreSubscriber<V> {

    @Override
    public void onNext(V v) {

    }

    @Override
    public void onError(Throwable t) {

    }

    @Override
    public void onComplete() {

    }

    @Override
    public void request(long n) {

    }

    @Override
    public void cancel() {

    }

    @Override
    public void onSubscribe(Subscription s) {

    }
}
