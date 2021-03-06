package io.penguin.pengiuncassandra.config;

import io.penguin.penguincore.plugin.PluginInput;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CassandraSourceConfig {
    private Class<?> valueType;
    private String table;
    private String idColumn;
    private String keySpace;
    private String columns;

    private PluginInput pluginInput;
}
