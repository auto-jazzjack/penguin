package io.penguin.pengiuncassandra.connection;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;

import java.util.Arrays;
import java.util.Optional;

public class CassandraConnection {

    private static Session session;

    public synchronized static Session connect(CassandraConnectionConfig config) {
        if (session != null) {
            return session;
        }
        Cluster.Builder builder = Cluster.builder();

        Optional.of(config).map(CassandraConnectionConfig::getHosts)
                .map(i -> i.split(","))
                .map(Arrays::asList)
                .ifPresent(i -> builder.addContactPoints(i.toArray(String[]::new)));

        session = builder.build().connect(config.getKeySpace());
        return session;
    }
}
