package io.penguin.penguincore.plugin.circuit;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.penguin.penguincore.plugin.Ingredient.CircuitDecorator;
import io.penguin.penguincore.plugin.PluginInput;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CircuitConfigurationTest {


    @Test
    public void should_circuit_not_supported() {
        PluginInput pluginInput = PluginInput.builder()
                .build();
        CircuitConfiguration circuitConfiguration = new CircuitConfiguration(pluginInput);
        Assertions.assertFalse(circuitConfiguration.support());
    }

    @Test
    public void should_circuit_supported() {
        PluginInput pluginInput = PluginInput.builder()
                .circuit(CircuitModel.base().build())
                .build();
        CircuitConfiguration circuitConfiguration = new CircuitConfiguration(pluginInput);
        Assertions.assertTrue(circuitConfiguration.support());
    }


    @Test
    public void should_circuit_generated() {
        PluginInput pluginInput = PluginInput.builder()
                .circuit(CircuitModel.builder()
                        .circuitName("circuit")
                        .waitDurationInOpenStateMillisecond(123)
                        .permittedNumberOfCallsInHalfOpenState(15)
                        .failureRateThreshold(14.1f)
                        .build())
                .build();
        CircuitConfiguration circuitConfiguration = new CircuitConfiguration(pluginInput);

        CircuitDecorator generate = circuitConfiguration.generate(this.getClass());

        Assertions.assertEquals(CircuitConfiguration.fail, generate.getFail().getId().getName());
        Assertions.assertEquals(CircuitConfiguration.success, generate.getSuccess().getId().getName());
        Assertions.assertEquals("circuit", generate.getCircuitBreaker().getName());

        CircuitBreakerConfig circuitBreakerConfig = generate.getCircuitBreaker().getCircuitBreakerConfig();
        Assertions.assertEquals(14.1f, circuitBreakerConfig.getFailureRateThreshold());
        Assertions.assertEquals(15, circuitBreakerConfig.getPermittedNumberOfCallsInHalfOpenState());
        Assertions.assertEquals(123, circuitBreakerConfig.getWaitDurationInOpenState().toMillis());

    }

}
