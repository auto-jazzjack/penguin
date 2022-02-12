package io.penguin.pengiuncassandra.config;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import io.penguin.penguincore.plugin.PluginInput;
import io.penguin.penguincore.plugin.timeout.TimeoutModel;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Data
@Builder
public class CassandraIngredient {
    private Class<?> valueType;
    private Session session;
    private String idColumn;
    private String keyspace;
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
        Objects.requireNonNull(config.getHosts());
        Objects.requireNonNull(config.getTable());
        Objects.requireNonNull(config.getValueType());
        Objects.requireNonNull(config.getKeySpace());

        CassandraIngredient ingredient = CassandraIngredient.base().build();
        Cluster.Builder builder = Cluster.builder();

        Optional.of(config).map(CassandraSourceConfig::getHosts).ifPresent(i -> builder.addContactPoints(i.toArray(String[]::new)));
        Optional.of(config).map(CassandraSourceConfig::getPort).ifPresent(builder::withPort);
        Optional.of(config).map(CassandraSourceConfig::getValueType).ifPresent(ingredient::setValueType);
        Optional.of(config).map(CassandraSourceConfig::getIdColumn).ifPresent(ingredient::setIdColumn);
        Optional.of(config).map(CassandraSourceConfig::getTable).ifPresent(ingredient::setTable);
        Optional.of(config).map(CassandraSourceConfig::getKeySpace).ifPresent(ingredient::setKeyspace);
        Optional.of(config).map(CassandraSourceConfig::getColumns).ifPresent(ingredient::setColumns);

        Optional.of(config).map(CassandraSourceConfig::getKeySpace)
                .filter(i -> !i.isEmpty())
                .ifPresent(i -> ingredient.setSession(builder.build().connect(i)));

        return ingredient;
    }
}

