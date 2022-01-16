package io.penguin.penguincore.plugin;

import io.penguin.penguincore.plugin.circuit.CircuitModel;
import io.penguin.penguincore.plugin.timeout.TimeoutModel;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PluginInput {

    private CircuitModel circuit;
    private TimeoutModel timeout;

    public static PluginInput.PluginInputBuilder base() {
        return PluginInput.builder()
                .circuit(CircuitModel.base().build())
                .timeout(TimeoutModel.base().build());
    }
}
