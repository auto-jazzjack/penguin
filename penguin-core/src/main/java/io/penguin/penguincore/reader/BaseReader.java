package io.penguin.penguincore.reader;

import io.penguin.penguincore.plugin.Ingredient.AllIngredient;
import io.penguin.penguincore.plugin.Plugin;
import io.penguin.penguincore.plugin.PluginInput;
import io.penguin.penguincore.plugin.bulkhead.BulkheadConfiguration;
import io.penguin.penguincore.plugin.bulkhead.BulkheadPlugin;
import io.penguin.penguincore.plugin.circuit.CircuitConfiguration;
import io.penguin.penguincore.plugin.circuit.CircuitPlugin;
import io.penguin.penguincore.plugin.timeout.TimeoutConfiguration;
import io.penguin.penguincore.plugin.timeout.TimeoutPlugin;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseReader<K, V> implements Reader<K, V> {

    private final Reader<K, V> delegated;
    private final Plugin[] plugins;

    public BaseReader(Reader<K, V> delegated) {
        this.delegated = delegated;
        PluginInput pluginInput = this.pluginInput();

        List<Plugin<Object>> pluginList = new ArrayList<>();
        AllIngredient ingredient = AllIngredient.builder().build();

        TimeoutConfiguration timeoutConfiguration = new TimeoutConfiguration(pluginInput);
        if (timeoutConfiguration.support()) {
            ingredient.setTimeoutIngredient(timeoutConfiguration.generate(this.getClass()));
            pluginList.add(new TimeoutPlugin<>(ingredient.getTimeoutIngredient()));
        }

        BulkheadConfiguration bulkheadConfiguration = new BulkheadConfiguration(pluginInput);
        if (bulkheadConfiguration.support()) {
            ingredient.setBulkheadIngredient(bulkheadConfiguration.generate(this.getClass()));
            pluginList.add(new BulkheadPlugin<>(ingredient.getBulkheadIngredient()));
        }

        CircuitConfiguration circuitConfiguration = new CircuitConfiguration(pluginInput);
        if (circuitConfiguration.support()) {
            ingredient.setCircuitIngredient(circuitConfiguration.generate(this.getClass()));
            pluginList.add(new CircuitPlugin<>(ingredient.getCircuitIngredient()));
        }

        plugins = pluginList.toArray(new Plugin[0]);
    }

    abstract public Mono<V> forNormal(K key);

    public V forFallback(K key) {
        return null;
    }


    public Mono<V> findOne(K key) {
        Mono<V> publish = findOne(key);
        for (Plugin plugin : plugins) {
            publish = plugin.decorateSource(publish);
        }
        if (forFallback(key) != null) {
            publish.onErrorReturn(forFallback(key));
        }
        return publish;
    }

    public PluginInput pluginInput() {
        return PluginInput.base().build();
    }
}
