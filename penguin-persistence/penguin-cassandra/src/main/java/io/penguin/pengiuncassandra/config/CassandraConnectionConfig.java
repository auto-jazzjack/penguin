package io.penguin.pengiuncassandra.config;

import io.penguin.penguincore.plugin.PluginInput;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CassandraConnectionConfig {
    private String keySpace;
    private String hosts;
    private Integer port;

    private PluginInput pluginInput;
}
