package io.penguin.penguincore.plugin.bulkhead;

import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadConfig;
import io.github.resilience4j.reactor.bulkhead.operator.BulkheadOperator;
import io.penguin.penguincore.metric.MetricCreator;
import io.penguin.penguincore.plugin.BulkheadDecorator;
import io.penguin.penguincore.plugin.PluginGenerator;

import java.time.Duration;
import java.util.Optional;

public class BulkheadGenerator<V> implements PluginGenerator<BulkheadDecorator<V>> {
    private final BulkheadModel bulkheadModel;

    public BulkheadGenerator(BulkheadModel bulkheadModel) {
        this.bulkheadModel = bulkheadModel;
    }

    @Override
    public boolean support() {
        boolean empty = Optional.ofNullable(this.bulkheadModel)
                .isEmpty();
        return !empty;
    }


    static final String fail = "bulkhead_rejected";
    static final String success = "bulkhead_success";

    @Override
    public BulkheadDecorator<V> generate(Class<?> clazz) {
        BulkheadOperator<V> of = BulkheadOperator.of(Bulkhead.of(clazz.getSimpleName(), BulkheadConfig.custom()
                .maxConcurrentCalls(this.bulkheadModel.getMaxConcurrentCalls())
                .maxWaitDuration(Duration.ofMillis(this.bulkheadModel.getMaxWaitDurationMilliseconds()))
                .build()));

        return BulkheadDecorator.<V>builder()
                .bulkheadOperator(of)
                .fail(MetricCreator.counter(fail, "kind", clazz.getSimpleName()))
                .build();
    }
}
