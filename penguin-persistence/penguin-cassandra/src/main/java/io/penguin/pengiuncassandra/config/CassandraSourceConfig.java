package io.penguin.pengiuncassandra.config;

import io.penguin.penguincore.plugin.bulkhead.BulkheadModel;
import io.penguin.penguincore.plugin.circuit.CircuitModel;
import io.penguin.penguincore.plugin.timeout.TimeoutModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CassandraSourceConfig<V> {
    private Class<V> valueType;
    private CircuitModel circuit;
    private TimeoutModel timeout;
    private BulkheadModel bulkhead;
}
