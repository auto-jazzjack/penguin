package io.penguin.penguincore.plugin;

import io.penguin.penguincore.plugin.circuit.CircuitModel;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PluginInput {

    private CircuitModel circuit;

    public static PluginInput.PluginInputBuilder base() {
        return PluginInput.builder().circuit(CircuitModel.base().build());
    }
}
