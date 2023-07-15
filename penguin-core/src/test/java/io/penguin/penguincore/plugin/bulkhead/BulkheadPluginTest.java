package io.penguin.penguincore.plugin.bulkhead;

import io.github.resilience4j.bulkhead.BulkheadFullException;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.penguin.penguincore.plugin.BulkheadDecorator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

@Slf4j
public class BulkheadPluginTest {

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

        BulkheadGenerator bulkheadConfiguration = new BulkheadGenerator(BulkheadModel.base().build());

        BulkheadDecorator generate = bulkheadConfiguration.generate(this.getClass());
        BulkHeadOperator<String> objectBulkheadPlugin = new BulkHeadOperator<>(generate);

        for (int i = 0; i < 5; i++) {
            String hello = objectBulkheadPlugin.decorateSource(Mono.just("hello")).block();
            Assertions.assertEquals("hello", hello);
        }

        Assertions.assertEquals(5, generate.getSuccess().count());
    }

    @Test
    public void should_call_failed_with_concurrent_call_rejected() {

        BulkheadGenerator bulkheadConfiguration = new BulkheadGenerator(BulkheadModel.base().maxConcurrentCalls(0).build());
        BulkheadDecorator generate = bulkheadConfiguration.generate(this.getClass());
        BulkHeadOperator<String> objectBulkheadPlugin = new BulkHeadOperator<>(generate);

        for (int i = 0; i < 5; i++) {
            Assertions.assertThrows(BulkheadFullException.class, () -> {
                objectBulkheadPlugin.decorateSource(Mono.just("hello")).block();
            });
        }

        Assertions.assertEquals(5, generate.getFail().count());
    }

}
