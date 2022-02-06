package io.penguin.penguincore.plugin.bulkhead;

import io.github.resilience4j.bulkhead.BulkheadFullException;
import io.penguin.penguincore.plugin.Ingredient.BulkheadIngredient;
import io.penguin.penguincore.plugin.PluginInput;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import reactor.core.publisher.Mono;

@Slf4j
public class BulkheadPluginTest {


    @Test
    public void should_call_succeed() {

        BulkheadConfiguration bulkheadConfiguration = new BulkheadConfiguration(PluginInput.builder()
                .bulkhead(BulkheadModel.base().build())
                .build());

        BulkheadIngredient generate = bulkheadConfiguration.generate(this.getClass());
        BulkheadPlugin<String> objectBulkheadPlugin = new BulkheadPlugin<>(generate);

        String hello = objectBulkheadPlugin.decorateSource(Mono.just("hello")).block();

        Assertions.assertEquals("hello", hello);
        Assertions.assertEquals(1, generate.getSuccess().count());
    }

    @Test
    public void should_call_failed_with_concurrent_call_rejected() {

        BulkheadConfiguration bulkheadConfiguration = new BulkheadConfiguration(PluginInput.builder()
                .bulkhead(BulkheadModel.base().maxConcurrentCalls(0).build())
                .build());
        BulkheadIngredient generate = bulkheadConfiguration.generate(this.getClass());

        Assertions.assertThrows(BulkheadFullException.class, () -> {
            BulkheadPlugin<String> objectBulkheadPlugin = new BulkheadPlugin<>(generate);
            objectBulkheadPlugin.decorateSource(Mono.just("hello")).block();
        });

        Assertions.assertEquals(1, generate.getFail().count());
    }

}
