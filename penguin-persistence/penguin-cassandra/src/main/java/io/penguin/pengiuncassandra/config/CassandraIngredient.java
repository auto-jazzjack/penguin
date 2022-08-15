package io.penguin.pengiuncassandra.config;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;
import io.penguin.penguincore.plugin.PluginInput;
import io.penguin.penguincore.plugin.timeout.TimeoutModel;
import lombok.Builder;
import lombok.Data;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;


@Data
@Builder
public class CassandraIngredient {
    private Class<?> valueType;
    private List<String> idColumn;
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
        Objects.requireNonNull(config.getValueType());

        CassandraIngredient ingredient = CassandraIngredient.base().build();

        ingredient.setValueType(config.getValueType());

        //Partition Key
        ingredient.setColumns(Optional.of(config.getValueType())
                .map(i -> Arrays.stream(i.getDeclaredFields())
                        .filter(j -> j.isAnnotationPresent(PartitionKey.class))
                        .map(Field::getName)
                        .collect(Collectors.toList()))
                .orElseThrow(() -> new IllegalArgumentException("PartitionKey annotation should exist"))
        );

        //Target column
        ingredient.setIdColumn(Optional.of(config.getValueType())
                .map(i -> Arrays.stream(i.getDeclaredFields())
                        .map(j -> {
                            if (j.isAnnotationPresent(Column.class)) {
                                return j.getAnnotation(Column.class).name();
                            } else {
                                return j.getName();
                            }
                        })
                        .collect(Collectors.toList()))
                .orElseThrow(() -> new IllegalArgumentException("Column annotation should exist"))
        );

        //Table Name
        ingredient.setTable(Optional.of(config.getValueType())
                .filter(i -> i.isAnnotationPresent(Table.class))
                .map(i -> i.getAnnotation(Table.class))
                .orElseThrow(() -> new IllegalArgumentException("Table annotation should exist"))
                .name());


        return ingredient;
    }
}

