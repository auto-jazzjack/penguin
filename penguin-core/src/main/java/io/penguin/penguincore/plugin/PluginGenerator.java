package io.penguin.penguincore.plugin;

public interface PluginGenerator<V> {

    boolean support();

    V generate(Class<?> clazz);

}
