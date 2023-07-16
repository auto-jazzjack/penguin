package io.penguin.penguincore.plugin.timeout;

import io.netty.util.HashedWheelTimer;
import io.penguin.penguincore.metric.MetricCreator;
import io.penguin.penguincore.plugin.TimeoutDecorator;
import io.penguin.penguincore.plugin.PluginGenerator;

import java.util.Optional;

public class TimeoutGenerator implements PluginGenerator<TimeoutDecorator> {

    private final TimeoutModel timeoutModel;

    private static final HashedWheelTimer hashedWheelTimer = new HashedWheelTimer();

    public TimeoutGenerator(TimeoutModel timeoutModel) {
        this.timeoutModel = timeoutModel;
    }

    @Override
    public boolean support() {
        boolean empty = Optional.ofNullable(timeoutModel)
                .isEmpty();
        return !empty;
    }

    static final String fail = "time_out";

    @Override
    public TimeoutDecorator generate(Class<?> clazz) {
        return TimeoutDecorator.builder()
                .milliseconds(timeoutModel.getTimeoutMilliseconds())
                .timer(hashedWheelTimer)
                .fail(MetricCreator.counter(fail, "kind", clazz.getSimpleName()))
                .build();
    }
}
