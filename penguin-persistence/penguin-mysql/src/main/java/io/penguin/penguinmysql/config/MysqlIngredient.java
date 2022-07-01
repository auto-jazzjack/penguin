package io.penguin.penguinmysql.config;

import io.penguin.penguincore.plugin.PluginInput;
import io.penguin.penguincore.plugin.timeout.TimeoutModel;
import lombok.Builder;
import lombok.Data;

import java.util.Objects;
import java.util.Optional;


@Data
@Builder
public class MysqlIngredient {

    private String driverClass;
    private String url;
    private String userName;
    private String password;
    private PluginInput pluginInput;


    public static MysqlIngredient.MysqlIngredientBuilder base() {
        return MysqlIngredient.builder()
                .pluginInput(PluginInput.builder()
                        .timeout(TimeoutModel.builder()
                                .timeoutMilliseconds(300)
                                .build())
                        .build());
    }

    public static MysqlIngredient toInternal(MysqlSourceConfig config) {

        Objects.requireNonNull(config);
        Objects.requireNonNull(config.getUrl());
        Objects.requireNonNull(config.getDriverClassName());

        MysqlIngredient ingredient = MysqlIngredient.base().build();


        Optional.of(config).map(MysqlSourceConfig::getDriverClassName).ifPresent(ingredient::setDriverClass);
        Optional.of(config).map(MysqlSourceConfig::getUrl).ifPresent(ingredient::setUrl);
        Optional.of(config).map(MysqlSourceConfig::getPassword).ifPresent(ingredient::setPassword);
        Optional.of(config).map(MysqlSourceConfig::getUserName).ifPresent(ingredient::setUserName);


        return ingredient;
    }
}

