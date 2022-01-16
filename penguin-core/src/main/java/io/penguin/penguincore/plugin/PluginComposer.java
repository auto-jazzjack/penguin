package io.penguin.penguincore.plugin;


import io.penguin.penguincore.plugin.circuit.CircuitPlugin;
import io.penguin.penguincore.plugin.timeout.TimeoutPlugin;
import reactor.core.publisher.Mono;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class PluginComposer {

    private static final List<Class<? extends Plugin>> pluginList;

    static {
        pluginList = new ArrayList<>();
        pluginList.add(CircuitPlugin.class);
        pluginList.add(TimeoutPlugin.class);
    }

    public static <V> Plugin<V>[] orderedPlugin(PluginInput input) throws Exception {
        if (input == null) {
            return null;
        }

        List<Plugin<V>> plugins = new ArrayList<>();
        for (Class<? extends Plugin> aClass : pluginList) {
            Constructor<? extends Plugin> constructor = aClass.getConstructor(PluginInput.class, Mono.class);
            Plugin<V> plugin = constructor.newInstance(input, Mono.empty());

            if (plugin.support()) {
                plugins.add(plugin);
            }
        }

        Plugin<V>[] collect = plugins.stream()
                .sorted(Comparator.comparingInt(Plugin::order))
                .collect(Collectors.toList())
                .toArray(Plugin[]::new);

        return collect;

    }

}
