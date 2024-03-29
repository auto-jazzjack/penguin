package io.penguin.penguincore.plugin.timeout;

import io.penguin.penguincore.plugin.TimeoutDecorator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TimeoutConfigurationTest {


    @Test
    public void should_timeout_generated() {
        TimeoutGenerator timeoutConfiguration = new TimeoutGenerator(TimeoutModel.builder()
                .timeoutMilliseconds(1)
                .build());

        TimeoutDecorator generate = timeoutConfiguration.generate(this.getClass());

        Assertions.assertEquals(TimeoutGenerator.fail, generate.getFail().getId().getName());
        Assertions.assertEquals(1, generate.getMilliseconds());

    }

}
