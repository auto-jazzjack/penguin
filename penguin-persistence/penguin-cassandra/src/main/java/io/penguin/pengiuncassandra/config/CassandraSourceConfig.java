package io.penguin.pengiuncassandra.config;

import io.penguin.penguincore.plugin.PluginInput;
import lombok.Builder;
import lombok.Data;

import java.util.List;


@Data
@Builder
public class CassandraSourceConfig {
    //private PreparedStatement statement;
    private Class<?> valueType;
    private String table;
    private String idColumn;
    private String keySpace;
    private List<String> hosts;
    private Integer port;
    //private MappingManager mappingManager;

    private PluginInput pluginInput;
}
