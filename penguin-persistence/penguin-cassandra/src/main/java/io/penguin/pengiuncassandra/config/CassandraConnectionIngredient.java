package io.penguin.pengiuncassandra.config;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
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
public class CassandraConnectionIngredient {
    private Session session;
    private String keyspace;
    private PluginInput pluginInput;


    public static CassandraConnectionIngredient.CassandraConnectionIngredientBuilder base() {
        return CassandraConnectionIngredient.builder()
                .pluginInput(PluginInput.builder()
                        .timeout(TimeoutModel.builder()
                                .timeoutMilliseconds(300)
                                .build())
                        .build());
    }

    public static CassandraConnectionIngredient toInternal(CassandraConnectionConfig config) {

        Objects.requireNonNull(config);
        Objects.requireNonNull(config.getHosts());
        Objects.requireNonNull(config.getKeySpace());

        CassandraConnectionIngredient ingredient = CassandraConnectionIngredient.base().build();
        Cluster.Builder builder = Cluster.builder();

        Optional.of(config).map(CassandraConnectionConfig::getHosts)
                .map(i -> i.split(","))
                .map(Arrays::asList)
                .ifPresent(i -> builder.addContactPoints(i.toArray(String[]::new)));

        Optional.of(config).map(CassandraConnectionConfig::getKeySpace)
                .filter(i -> !i.isEmpty())
                .ifPresent(i -> ingredient.setSession(builder.build().connect(i)));

        Optional.of(config).map(CassandraConnectionConfig::getPort).ifPresent(builder::withPort);
        Optional.of(config).map(CassandraConnectionConfig::getKeySpace).ifPresent(ingredient::setKeyspace);


        return ingredient;
    }
}

