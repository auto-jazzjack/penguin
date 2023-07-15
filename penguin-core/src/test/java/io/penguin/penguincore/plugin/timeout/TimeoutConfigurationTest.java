package io.penguin.penguincore.plugin.timeout;

import io.penguin.penguincore.plugin.decorator.TimeoutDecorator;
import io.penguin.penguincore.plugin.PluginInput;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TimeoutConfigurationTest {


    @Test
    public void should_timeout_not_supported() {
        PluginInput pluginInput = PluginInput.builder()
                .build();
        TimeoutConfiguration timeoutConfiguration = new TimeoutConfiguration(pluginInput);
        Assertions.assertFalse(timeoutConfiguration.support());
    }

    @Test
    public void should_timeout_supported() {
        PluginInput pluginInput = PluginInput.builder()
                .timeout(TimeoutModel.base().build())
                .build();
        TimeoutConfiguration timeoutConfiguration = new TimeoutConfiguration(pluginInput);
        Assertions.assertTrue(timeoutConfiguration.support());
    }


    @Test
    public void should_timeout_generated() {
        PluginInput pluginInput = PluginInput.builder()
                .timeout(TimeoutModel.builder()
                        .timeoutMilliseconds(1)
                        .build())
                .build();
        TimeoutConfiguration timeoutConfiguration = new TimeoutConfiguration(pluginInput);

        TimeoutDecorator generate = timeoutConfiguration.generate(this.getClass());

        Assertions.assertEquals(TimeoutConfiguration.fail, generate.getFail().getId().getName());
        Assertions.assertEquals(1, generate.getMilliseconds());

    }

}
