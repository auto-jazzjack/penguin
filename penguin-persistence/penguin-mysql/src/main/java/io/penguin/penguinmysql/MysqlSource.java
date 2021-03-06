package io.penguin.penguinmysql;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import io.penguin.penguincore.metric.MetricCreator;
import io.penguin.penguincore.plugin.Ingredient.AllIngredient;
import io.penguin.penguincore.plugin.Plugin;
import io.penguin.penguincore.plugin.timeout.TimeoutConfiguration;
import io.penguin.penguincore.plugin.timeout.TimeoutPlugin;
import io.penguin.penguincore.reader.Context;
import io.penguin.penguincore.reader.Reader;
import io.penguin.penguinmysql.config.MysqlIngredient;
import io.penguin.penguinmysql.connection.MysqlConnection;
import reactor.core.publisher.Mono;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;


public class MysqlSource<K, V> implements Reader<K, Context<V>> {

    private final Plugin<Context<V>>[] plugins;
    private final Connection connection;
    private final Counter failed = MetricCreator.counter("mysql_reader",
            "kind", this.getClass().getSimpleName(), "type", "success");

    private final Counter success = MetricCreator.counter("mysql_reader",
            "kind", this.getClass().getSimpleName(), "type", "failed");

    private final Timer latency = MetricCreator.timer("mysql_latency",
            "kind", this.getClass().getSimpleName());

    public MysqlSource(MysqlConnection connection, MysqlIngredient mysqlIngredient) throws Exception {
        this.connection = MysqlConnection.connection(mysqlIngredient);
        AllIngredient ingredient = AllIngredient.builder().build();

        List<Plugin<Object>> pluginList = new ArrayList<>();

        TimeoutConfiguration timeoutConfiguration = new TimeoutConfiguration(mysqlIngredient.getPluginInput());
        if (timeoutConfiguration.support()) {
            ingredient.setTimeoutIngredient(timeoutConfiguration.generate(this.getClass()));
            pluginList.add(new TimeoutPlugin<>(ingredient.getTimeoutIngredient()));
        }
        plugins = pluginList.toArray(new Plugin[0]);

    }


    @Override
    public Mono<Context<V>> findOne(K key) {
        return Mono.empty();
        //this.connection.createStatement().ex
    }
}
