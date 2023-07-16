package io.penguin.penguincore.plugin.circuit;

import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.penguin.penguincore.plugin.CircuitDecorator;
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

        CircuitGenerator<String> circuitConfiguration = new CircuitGenerator<>(CircuitModel.base().build());

        CircuitDecorator<String> generate = circuitConfiguration.generate(this.getClass());
        CircuitPlugn<String> circuitPlugin = new CircuitPlugn<>(Mono.just("hello"), generate);

        for (int i = 0; i < 5; i++) {
            String hello = circuitPlugin.block();
            Assertions.assertEquals("hello", hello);
        }

        Assertions.assertEquals(5, generate.getSuccess().count());
    }

    @Test
    public void should_call_failed_with_circuit_opened() {

        CircuitGenerator<String> circuitConfiguration = new CircuitGenerator<>(CircuitModel.builder()
                .permittedNumberOfCallsInHalfOpenState(1)
                .failureRateThreshold(10f)
                .waitDurationInOpenStateMillisecond(Integer.MAX_VALUE)
                .build());
        CircuitDecorator<String> generate = circuitConfiguration.generate(this.getClass());
        CircuitPlugn<String> circuitPlugin = new CircuitPlugn<>(Mono.error(new IllegalArgumentException("hello")), generate);

        for (int i = 0; i < 100/*Minimum number of call */ + 10; i++) {
            Assertions.assertThrows(RuntimeException.class, circuitPlugin::block);
        }

        Assertions.assertEquals(OPEN, generate.getCircuitBreaker().getState());
        Assertions.assertEquals(10, generate.getFail().count());
    }

}
