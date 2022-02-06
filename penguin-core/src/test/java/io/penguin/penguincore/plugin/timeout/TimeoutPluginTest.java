package io.penguin.penguincore.plugin.timeout;

import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.penguin.penguincore.plugin.Ingredient.TimeoutIngredient;
import io.penguin.penguincore.plugin.PluginInput;
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

        TimeoutConfiguration timeoutConfiguration = new TimeoutConfiguration(PluginInput.builder()
                .timeout(TimeoutModel.base().build())
                .build());

        TimeoutIngredient generate = timeoutConfiguration.generate(this.getClass());
        TimeoutPlugin<String> circuitPlugin = new TimeoutPlugin<>(generate);


        String hello = circuitPlugin.decorateSource(Mono.just("hello")).block();
        Assertions.assertEquals("hello", hello);
    }

    @Test
    public void should_call_failed_with_timeout() {

        TimeoutConfiguration timeoutConfiguration = new TimeoutConfiguration(PluginInput.builder()
                .timeout(TimeoutModel.builder()
                        .timeoutMilliseconds(1)
                        .build())
                .build());
        TimeoutIngredient generate = timeoutConfiguration.generate(this.getClass());
        TimeoutPlugin<String> timeoutPlugin = new TimeoutPlugin<>(generate);

        for (int i = 0; i < 10; i++) {
            timeoutPlugin.decorateSource(Mono.create(j -> {
                        try {
                            Thread.sleep(10);
                        } catch (Exception e) {
                            j.error(e);
                        }

                        j.success("hello");
                    }))
                    .block();
        }

        Assertions.assertEquals(10, generate.getFail().count());
    }

}
