package io.penguin.penguinmysql.config;

import io.penguin.penguincore.plugin.PluginInput;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MysqlSourceConfig {
    private String driverClassName;
    private String url;
    private String userName;
    private String password;

    private PluginInput pluginInput;
}
