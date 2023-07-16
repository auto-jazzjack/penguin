package io.penguin.penguincore.plugin.circuit;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.penguin.penguincore.plugin.CircuitDecorator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CircuitConfigurationTest {


    @Test
    public void should_circuit_generated() {
        CircuitGenerator<String> circuitConfiguration = new CircuitGenerator<>(CircuitModel.builder()
                .circuitName("circuit")
                .waitDurationInOpenStateMillisecond(123)
                .permittedNumberOfCallsInHalfOpenState(15)
                .failureRateThreshold(14.1f)
                .build());

        CircuitDecorator<String> generate = circuitConfiguration.generate(this.getClass());

        Assertions.assertEquals(CircuitGenerator.fail, generate.getFail().getId().getName());
        Assertions.assertEquals(CircuitGenerator.success, generate.getSuccess().getId().getName());
        Assertions.assertEquals("circuit", generate.getCircuitBreaker().getName());

        CircuitBreakerConfig circuitBreakerConfig = generate.getCircuitBreaker().getCircuitBreakerConfig();
        Assertions.assertEquals(14.1f, circuitBreakerConfig.getFailureRateThreshold());
        Assertions.assertEquals(15, circuitBreakerConfig.getPermittedNumberOfCallsInHalfOpenState());
        Assertions.assertEquals(123, circuitBreakerConfig.getWaitDurationInOpenState().toMillis());

    }

}
