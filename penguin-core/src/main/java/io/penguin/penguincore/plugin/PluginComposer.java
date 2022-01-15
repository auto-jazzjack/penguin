package io.penguin.penguincore.plugin;


import io.penguin.penguincore.plugin.circuit.CircuitPlugin;
import io.penguin.penguincore.reader.Reader;
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
    }

    public static <K, V> Reader<K, V> decorateWithInput(PluginInput input, Reader<K, V> reader) throws Exception {

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

        return new Reader<>() {

            private final Plugin<V>[] plugins = collect;
            private final Reader<K, V> fromRemote = reader;

            @Override
            public Mono<V> findOne(K key) {

                Mono<V> mono = fromRemote.findOne(key);

                for (Plugin<V> plugin : plugins) {
                    mono = (Mono<V>) plugin.apply();
                }

                return mono;
            }
        };
    }
}
