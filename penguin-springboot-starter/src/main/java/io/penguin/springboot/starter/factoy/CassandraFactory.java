package io.penguin.springboot.starter.factoy;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.penguin.pengiuncassandra.CassandraSource;
import io.penguin.pengiuncassandra.config.CassandraSourceConfig;
import io.penguin.pengiuncassandra.connection.CassandraConnectionConfig;
import io.penguin.pengiuncassandra.connection.CassandraConnectionIngredient;
import io.penguin.penguincore.reader.Context;
import io.penguin.penguincore.reader.Reader;
import io.penguin.springboot.starter.mapper.ContainerKind;
import org.springframework.stereotype.Component;

import java.util.Map;

import static io.penguin.pengiuncassandra.config.CassandraIngredient.toInternal;

@Component
public class CassandraFactory implements ReaderFactory<CassandraConnectionConfig> {
    private static final ObjectMapper objectMapper = new ObjectMapper();


    public CassandraFactory() {
    }

    @Override
    public ContainerKind getContainerType() {
        return ContainerKind.CASSANDRA;
    }

    @Override
    public Reader<Object, Context<Object>> generate(CassandraConnectionConfig config, Map<String, Object> spec) {
        CassandraSourceConfig cassandraSourceConfig = objectMapper.convertValue(spec, CassandraSourceConfig.class);
        return new CassandraSource<>(CassandraConnectionIngredient.toInternal(config), toInternal(cassandraSourceConfig));
    }


}
