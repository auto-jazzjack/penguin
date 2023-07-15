package io.penguin.penguincore.plugin.timeout;

import io.netty.util.HashedWheelTimer;
import io.penguin.penguincore.metric.MetricCreator;
import io.penguin.penguincore.plugin.Ingredient.TimeoutDecorator;
import io.penguin.penguincore.plugin.PluginConfiguration;
import io.penguin.penguincore.plugin.PluginInput;

import java.util.Optional;

public class TimeoutConfiguration extends PluginConfiguration<TimeoutDecorator> {

    public TimeoutConfiguration(PluginInput pluginInput) {
        super(pluginInput);
    }

    public TimeoutConfiguration(TimeoutModel timeoutModel) {
        this(PluginInput.base()
                .timeout(timeoutModel)
                .build());
    }

    @Override
    public boolean support() {

        boolean empty = Optional.ofNullable(pluginInput)
                .map(PluginInput::getTimeout)
                .isEmpty();
        return !empty;
    }

    static final String fail = "time_out";

    @Override
    public TimeoutDecorator generate(Class<?> clazz) {
        return TimeoutDecorator.builder()
                .milliseconds(pluginInput.getTimeout().getTimeoutMilliseconds())
                .timer(new HashedWheelTimer())
                .fail(MetricCreator.counter(fail, "kind", clazz.getSimpleName()))
                .build();
    }
}
