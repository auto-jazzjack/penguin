package io.penguin.pengiuncassandra;

import com.datastax.driver.core.PreparedStatement;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class CassandraConfig {
    private PreparedStatement statement;
    private Class<?> valueType;
}
