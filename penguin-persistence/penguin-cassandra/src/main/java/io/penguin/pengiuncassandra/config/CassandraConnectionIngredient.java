package io.penguin.pengiuncassandra.config;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import io.penguin.pengiuncassandra.connect.CassandraConnection;
import lombok.Builder;
import lombok.Data;

import java.util.Objects;
import java.util.Optional;


@Data
@Builder
public class CassandraConnectionIngredient {
    private Session session;
    private String keyspace;


    public static CassandraConnectionIngredient.CassandraConnectionIngredientBuilder base() {
        return CassandraConnectionIngredient.builder();
    }

    public static CassandraConnectionIngredient toInternal(CassandraConnectionConfig config) {

        Objects.requireNonNull(config);
        Objects.requireNonNull(config.getHosts());
        Objects.requireNonNull(config.getKeySpace());

        CassandraConnectionIngredient ingredient = CassandraConnectionIngredient.base().build();
        Cluster.Builder builder = Cluster.builder();

        ingredient.setSession(CassandraConnection.connect(config));

        Optional.of(config).map(CassandraConnectionConfig::getPort).ifPresent(builder::withPort);
        Optional.of(config).map(CassandraConnectionConfig::getKeySpace).ifPresent(ingredient::setKeyspace);


        return ingredient;
    }
}

