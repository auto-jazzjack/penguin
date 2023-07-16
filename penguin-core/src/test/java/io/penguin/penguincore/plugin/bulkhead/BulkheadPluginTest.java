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

        BulkheadGenerator<String> bulkheadConfiguration = new BulkheadGenerator<>(BulkheadModel.base().build());

        BulkheadDecorator<String> generate = bulkheadConfiguration.generate(this.getClass());
        BulkHeadOperator<String> objectBulkheadPlugin = new BulkHeadOperator<>(Mono.just("hello"), generate);

        for (int i = 0; i < 5; i++) {
            Assertions.assertEquals("hello", objectBulkheadPlugin.block());
        }

        Assertions.assertEquals(5, generate.getSuccess().count());
    }

    @Test
    public void should_call_failed_with_concurrent_call_rejected() {

        BulkheadGenerator<String> bulkheadConfiguration = new BulkheadGenerator<>(BulkheadModel.base().maxConcurrentCalls(0).build());
        BulkheadDecorator<String> generate = bulkheadConfiguration.generate(this.getClass());
        BulkHeadOperator<String> objectBulkheadPlugin = new BulkHeadOperator<>(Mono.just("hello"), generate);

        for (int i = 0; i < 5; i++) {
            Assertions.assertThrows(BulkheadFullException.class, objectBulkheadPlugin::block);
        }

        Assertions.assertEquals(5, generate.getFail().count());
    }

}
