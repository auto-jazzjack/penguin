package io.penguin.penguincore.plugin.circuit;

import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.penguin.penguincore.plugin.Ingredient.CircuitDecorator;
import io.penguin.penguincore.plugin.PluginInput;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import static io.github.resilience4j.circuitbreaker.CircuitBreaker.State.OPEN;

@Slf4j
public class CircuitPluginTest {

    private final SimpleMeterRegistry simpleMeterRegistry = new SimpleMeterRegistry();

    @BeforeEach
    public void register() {
        Metrics.addRegistry(simpleMeterRegistry);
    }

    @AfterEach
    public void clean() {
        Metrics.removeRegistry(simpleMeterRegistry);
    }


    @Test
    public void should_call_succeed() {

        CircuitConfiguration circuitConfiguration = new CircuitConfiguration(PluginInput.builder()
                .circuit(CircuitModel.base().build())
                .build());

        CircuitDecorator generate = circuitConfiguration.generate(this.getClass());
        CircuitPlugin<String> circuitPlugin = new CircuitPlugin<>(generate);

        for (int i = 0; i < 5; i++) {
            String hello = circuitPlugin.decorateSource(Mono.just("hello")).block();
            Assertions.assertEquals("hello", hello);
        }

        Assertions.assertEquals(5, generate.getSuccess().count());
    }

    @Test
    public void should_call_failed_with_circuit_opened() {

        CircuitConfiguration circuitConfiguration = new CircuitConfiguration(PluginInput.builder()
                .circuit(CircuitModel.builder()
                        .permittedNumberOfCallsInHalfOpenState(1)
                        .failureRateThreshold(10f)
                        .waitDurationInOpenStateMillisecond(Integer.MAX_VALUE)
                        .build())
                .build());
        CircuitDecorator generate = circuitConfiguration.generate(this.getClass());
        CircuitPlugin<String> circuitPlugin = new CircuitPlugin<>(generate);

        for (int i = 0; i < 100/*Minimum number of call */ + 10; i++) {
            Assertions.assertThrows(RuntimeException.class, () ->
                    circuitPlugin.decorateSource(Mono.error(new IllegalArgumentException("hello"))).block());
        }

        Assertions.assertEquals(OPEN, generate.getCircuitBreaker().getState());
        Assertions.assertEquals(10, generate.getFail().count());
    }

}
