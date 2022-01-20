package io.penguin.penguincore.plugin;

public abstract class PluginConfiguration<V> {

    protected final PluginInput pluginInput;

    public PluginConfiguration(PluginInput pluginInput) {
        this.pluginInput = pluginInput;
    }

    abstract public boolean support();

    abstract public V generate(Class<?> clazz);

}
