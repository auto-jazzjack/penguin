package io.penguin.pengiuncassandra.config;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;
import io.penguin.penguincore.plugin.PluginInput;
import io.penguin.penguincore.plugin.timeout.TimeoutModel;
import io.penguin.penguincore.reader.Reader;
import lombok.Builder;
import lombok.Data;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;


@Data
@Builder
public class CassandraIngredient {
    private PreparedStatement statement;
    private Class<?> valueType;
    private Session session;
    private String idColumn;

    private PluginInput pluginInput;


    public static CassandraIngredient.CassandraIngredientBuilder base() {
        return CassandraIngredient.builder()
                .pluginInput(PluginInput.builder()
                        .timeout(TimeoutModel.builder()
                                .timeoutMilliseconds(300)
                                .build())
                        .build());
    }

    public static CassandraIngredient toInternal(CassandraSourceConfig config, Map<String, Reader> readers) {
        Objects.requireNonNull(config);

        CassandraIngredient ingredient = CassandraIngredient.base()
                .build();
        Cluster.Builder builder = Cluster.builder();

        Optional.of(config).map(CassandraSourceConfig::getHosts).ifPresent(i -> builder.addContactPoints(i.toArray(String[]::new)));
        Optional.of(config).map(CassandraSourceConfig::getPort).ifPresent(builder::withPort);
        Optional.of(config).map(CassandraSourceConfig::getValueType).ifPresent(ingredient::setValueType);
        Optional.of(config).map(CassandraSourceConfig::getIdColumn).ifPresent(ingredient::setIdColumn);

        Optional.of(config).map(CassandraSourceConfig::getKeySpace)
                .filter(i -> !i.isEmpty())
                .ifPresent(i -> ingredient.setSession(builder.build().connect(i)));

        return ingredient;
    }
}

