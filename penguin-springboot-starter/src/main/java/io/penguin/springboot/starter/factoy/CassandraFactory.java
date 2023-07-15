package io.penguin.springboot.starter.factoy;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.penguin.pengiuncassandra.CassandraSource;
import io.penguin.pengiuncassandra.config.CassandraSourceConfig;
import io.penguin.pengiuncassandra.connection.CassandraConnectionConfig;
import io.penguin.pengiuncassandra.connection.CassandraConnectionIngredient;
import io.penguin.penguincore.reader.Reader;
import io.penguin.springboot.starter.mapper.ContainerKind;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

import static io.penguin.pengiuncassandra.config.CassandraIngredient.toInternal;
import static io.penguin.springboot.starter.mapper.ContainerKind.CASSANDRA;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CassandraFactory<K, V> implements ReaderFactory<K, V> {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, Map<String, Object>> collectedResources;


    @Override
    public ContainerKind getContainerType() {
        return ContainerKind.CASSANDRA;
    }

    @Override
    public Reader<K, V> generate(Map<String, Object> spec) {
        CassandraConnectionConfig config = objectMapper.convertValue(collectedResources.get(CASSANDRA.name()), CassandraConnectionConfig.class);
        CassandraSourceConfig cassandraSourceConfig = objectMapper.convertValue(spec, CassandraSourceConfig.class);
        return new CassandraSource<>(CassandraConnectionIngredient.toInternal(config), toInternal(cassandraSourceConfig));
    }


}
