package io.penguin.pengiuncassandra.config;

import io.penguin.penguincore.plugin.PluginInput;
import io.penguin.penguincore.plugin.timeout.TimeoutModel;
import lombok.Builder;
import lombok.Data;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Data
@Builder
public class CassandraIngredient {
    private Class<?> valueType;
    private String idColumn;
    private String table;
    private List<String> columns;
    private PluginInput pluginInput;


    public static CassandraIngredient.CassandraIngredientBuilder base() {
        return CassandraIngredient.builder()
                .pluginInput(PluginInput.builder()
                        .timeout(TimeoutModel.builder()
                                .timeoutMilliseconds(300)
                                .build())
                        .build());
    }

    public static CassandraIngredient toInternal(CassandraSourceConfig config) {

        Objects.requireNonNull(config);
        Objects.requireNonNull(config.getTable());
        Objects.requireNonNull(config.getValueType());

        CassandraIngredient ingredient = CassandraIngredient.base().build();


        Optional.of(config).map(CassandraSourceConfig::getValueType).ifPresent(ingredient::setValueType);
        Optional.of(config).map(CassandraSourceConfig::getIdColumn).ifPresent(ingredient::setIdColumn);
        Optional.of(config).map(CassandraSourceConfig::getTable).ifPresent(ingredient::setTable);
        Optional.of(config).map(CassandraSourceConfig::getColumns).map(i -> i.split(","))
                .map(Arrays::asList).ifPresent(ingredient::setColumns);


        return ingredient;
    }
}

