package io.penguin.penguincore.plugin.timeout;

import io.netty.util.HashedWheelTimer;
import io.penguin.penguincore.metric.MetricCreator;
import io.penguin.penguincore.plugin.Ingredient.TimeoutIngredient;
import io.penguin.penguincore.plugin.PluginConfiguration;
import io.penguin.penguincore.plugin.PluginInput;

import java.util.Optional;

public class TimeoutConfiguration extends PluginConfiguration<TimeoutIngredient> {

    public TimeoutConfiguration(PluginInput pluginInput) {
        super(pluginInput);
    }

    @Override
    public boolean support() {

        boolean empty = Optional.ofNullable(pluginInput)
                .map(PluginInput::getTimeout)
                .isEmpty();
        return !empty;
    }

    private static final String timeout = "time_out";

    @Override
    public TimeoutIngredient generate(Class<?> clazz) {
        return TimeoutIngredient.builder()
                .milliseconds(pluginInput.getTimeout().getTimeoutMilliseconds())
                .timer(new HashedWheelTimer())
                .counter(MetricCreator.counter(timeout, "kind", clazz.getSimpleName()))
                .build();
    }
}
