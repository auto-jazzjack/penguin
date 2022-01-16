package io.penguin.penguincore.plugin;

public abstract class Pluggable<V> {

    protected final PluginInput pluginInput;

    public Pluggable(PluginInput pluginInput) {
        this.pluginInput = pluginInput;
    }

    abstract public boolean support();

    abstract public V generate();

}
