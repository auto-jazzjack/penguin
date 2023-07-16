package io.penguin.penguincore.plugin;

public interface PluginGenerator<V> {
    V generate(Class<?> clazz);
}
