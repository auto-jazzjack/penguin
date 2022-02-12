package io.penguin.pengiuncassandra.util;

import com.datastax.driver.core.Session;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.datastax.driver.mapping.MappingManager;

import java.util.List;

public class CasandraUtil {

    public static <V> MappingManager mappingManager(Session session, Class<? extends V> valueType, String keyspace) {
        MappingManager mappingManager = new MappingManager(session);
        mappingManager.mapper(valueType, keyspace);
        return mappingManager;
    }

    public static Select queryGenerator(String keyspace, String table, List<String> columns, String idColumn) {

        Select.Selection query = QueryBuilder.select();

        for (String i : columns) {
            query = query.column(i);
        }

        return query.from(keyspace, table)
                .where(QueryBuilder.eq(idColumn, QueryBuilder.bindMarker()))
                .limit(1);

    }
}
