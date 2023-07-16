package io.penguin.penguincore.plugin.bulkhead;

import io.penguin.penguincore.plugin.BulkheadDecorator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BulkheadConfigurationTest {


    @Test
    public void should_bulkhead_generated() {
        BulkheadGenerator<String> bulkheadConfiguration = new BulkheadGenerator<>(BulkheadModel.base().build());

        BulkheadDecorator<String> generate = bulkheadConfiguration.generate(this.getClass());

        Assertions.assertEquals(BulkheadGenerator.fail, generate.getFail().getId().getName());
        Assertions.assertEquals(BulkheadGenerator.success, generate.getSuccess().getId().getName());

    }

}
