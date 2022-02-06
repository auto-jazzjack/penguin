package io.penguin.penguincore.plugin.bulkhead;

import io.penguin.penguincore.plugin.Ingredient.BulkheadIngredient;
import io.penguin.penguincore.plugin.PluginInput;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BulkheadConfigurationTest {


    @Test
    public void should_bulk_head_not_supported() {
        PluginInput pluginInput = PluginInput.builder()
                .build();
        BulkheadConfiguration bulkheadConfiguration = new BulkheadConfiguration(pluginInput);
        Assertions.assertFalse(bulkheadConfiguration.support());
    }

    @Test
    public void should_bulk_head_supported() {
        PluginInput pluginInput = PluginInput.builder()
                .bulkhead(BulkheadModel.base().build())
                .build();
        BulkheadConfiguration bulkheadConfiguration = new BulkheadConfiguration(pluginInput);
        Assertions.assertTrue(bulkheadConfiguration.support());
    }


    @Test
    public void should_bulk_head_generated() {
        PluginInput pluginInput = PluginInput.builder()
                .bulkhead(BulkheadModel.base().build())
                .build();
        BulkheadConfiguration bulkheadConfiguration = new BulkheadConfiguration(pluginInput);


        BulkheadIngredient generate = bulkheadConfiguration.generate(this.getClass());

        Assertions.assertEquals(BulkheadConfiguration.fail, generate.getFail().getId().getName());
        Assertions.assertEquals(BulkheadConfiguration.success, generate.getSuccess().getId().getName());

    }

}
