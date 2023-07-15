package io.penguin.penguincore.plugin.timeout;

import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.penguin.penguincore.exception.TimeoutException;
import io.penguin.penguincore.plugin.TimeoutDecorator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

@Slf4j
public class TimeoutPluginTest {

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

        TimeoutGenerator timeoutConfiguration = new TimeoutGenerator(TimeoutModel.base().build());

        TimeoutDecorator generate = timeoutConfiguration.generate(this.getClass());
        TimeoutOperator<String> circuitPlugin = new TimeoutOperator<>(generate);


        String hello = circuitPlugin.decorateSource(Mono.just("hello")).block();
        Assertions.assertEquals("hello", hello);
    }

    @Test
    public void should_call_failed_with_timeout() {

        TimeoutGenerator timeoutConfiguration = new TimeoutGenerator(TimeoutModel.builder()
                .timeoutMilliseconds(1)
                .build());
        TimeoutDecorator generate = timeoutConfiguration.generate(this.getClass());
        TimeoutOperator<String> timeoutPlugin = new TimeoutOperator<>(generate);

        for (int i = 0; i < 10; i++) {
            try {
                timeoutPlugin.decorateSource(Mono.create(j -> {
                            try {
                                Thread.sleep(10);
                            } catch (Exception e) {
                                j.error(e);
                            }

                            j.success("hello");
                        }))
                        .block();
            } catch (Exception e) {
                Assertions.assertEquals(TimeoutException.class, e.getClass());
            }
        }

        Assertions.assertEquals(10, generate.getFail().count());
    }

}
