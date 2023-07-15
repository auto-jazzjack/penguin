package io.penguin.pengiuncassandra.util;

import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CasandraUtil {


    public static <V> Select queryGenerator(Class<V> valueType) {

        Select.Selection query = QueryBuilder.select();

        for (String i : columns(valueType)) {
            query = query.column(i);
        }

        Select from = query.from(keyspace(valueType), table(valueType));
        idColumn(valueType).forEach(i -> from.where(QueryBuilder.eq(i, QueryBuilder.bindMarker())));
        from.limit(10);

        return from;
    }

    private static <V> String table(Class<V> valueType) {
        return Optional.of(valueType)
                .map(i -> i.getAnnotation(Table.class))
                .map(Table::name)
                .orElseThrow(() -> new IllegalArgumentException("Table should exist"));
    }

    private static <V> String keyspace(Class<V> valueType) {
        return Optional.of(valueType)
                .map(i -> i.getAnnotation(Table.class))
                .map(Table::keyspace)
                .orElseThrow(() -> new IllegalArgumentException("keyspace should exist"));

    }

    private static <V> List<String> idColumn(Class<V> valueType) {

        return Optional.of(valueType)
                .map(i -> Arrays.stream(i.getDeclaredFields())
                        .filter(j -> j.isAnnotationPresent(PartitionKey.class))
                        .map(Field::getName)
                        .collect(Collectors.toList()))
                .orElseThrow(() -> new IllegalArgumentException("PartitionKey annotation should exist"));
    }

    private static <V> List<String> columns(Class<V> valueType) {
        return Optional.of(valueType)
                .map(i -> Arrays.stream(i.getDeclaredFields())
                        .map(j -> {
                            if (j.isAnnotationPresent(Column.class)) {
                                return j.getAnnotation(Column.class).name();
                            } else {
                                return j.getName();
                            }
                        })
                        .collect(Collectors.toList()))
                .orElseThrow(() -> new IllegalArgumentException("Column annotation should exist"));
    }


}
