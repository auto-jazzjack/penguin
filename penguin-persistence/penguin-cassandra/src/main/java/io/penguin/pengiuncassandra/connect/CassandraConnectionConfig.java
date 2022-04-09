package io.penguin.pengiuncassandra.connect;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CassandraConnectionConfig {
    private String keySpace;
    private String hosts;
    private Integer port;

}
