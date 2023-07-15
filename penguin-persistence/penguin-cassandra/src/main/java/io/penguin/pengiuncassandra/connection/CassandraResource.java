package io.penguin.pengiuncassandra.connection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CassandraResource {
    private String keySpace;
    private String hosts;
    private Integer port;

}
