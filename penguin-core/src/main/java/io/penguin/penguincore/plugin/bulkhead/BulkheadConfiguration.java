package io.penguin.penguincore.plugin.bulkhead;

import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadConfig;
import io.github.resilience4j.reactor.bulkhead.operator.BulkheadOperator;
import io.penguin.penguincore.metric.MetricCreator;
import io.penguin.penguincore.plugin.Ingredient.BulkheadDecorator;
import io.penguin.penguincore.plugin.PluginConfiguration;
import io.penguin.penguincore.plugin.PluginInput;

import java.time.Duration;
import java.util.Optional;

public class BulkheadConfiguration extends PluginConfiguration<BulkheadDecorator> {

    public BulkheadConfiguration(PluginInput pluginInput) {
        super(pluginInput);
    }

    @Override
    public boolean support() {
        boolean empty = Optional.ofNullable(pluginInput)
                .map(PluginInput::getBulkhead)
                .isEmpty();
        return !empty;
    }


    static final String fail = "bulkhead_rejected";
    static final String success = "bulkhead_success";

    @Override
    public BulkheadDecorator generate(Class<?> clazz) {
        BulkheadOperator<?> of = BulkheadOperator.of(Bulkhead.of(clazz.getSimpleName(), BulkheadConfig.custom()
                .maxConcurrentCalls(pluginInput.getBulkhead().getMaxConcurrentCalls())
                .maxWaitDuration(Duration.ofMillis(pluginInput.getBulkhead().getMaxWaitDurationMilliseconds()))
                .build()));

        return BulkheadDecorator.builder()
                .bulkheadOperator(of)
                .fail(MetricCreator.counter(fail, "kind", clazz.getSimpleName()))
                .success(MetricCreator.counter(success, "kind", clazz.getSimpleName()))
                .build();
    }
}
