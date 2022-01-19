package io.penguin.penguincore.plugin.timeout;

import io.netty.util.HashedWheelTimer;
import io.penguin.penguincore.plugin.Ingredient.TimeoutIngredient;
import io.penguin.penguincore.plugin.Pluggable;
import io.penguin.penguincore.plugin.PluginInput;

import java.util.Optional;

public class TimeoutPluggable extends Pluggable<TimeoutIngredient> {

    public TimeoutPluggable(PluginInput pluginInput) {
        super(pluginInput);
    }

    @Override
    public boolean support() {

        boolean empty = Optional.ofNullable(pluginInput)
                .map(PluginInput::getTimeout)
                .isEmpty();
        return !empty;
    }

    @Override
    public TimeoutIngredient generate() {
        return TimeoutIngredient.builder()
                .milliseconds(pluginInput.getTimeout().getTimeoutMilliseconds())
                .timer(new HashedWheelTimer())
                .build();
    }
}
