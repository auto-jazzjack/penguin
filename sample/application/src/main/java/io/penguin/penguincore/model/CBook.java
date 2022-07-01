package io.penguin.penguincore.model;

import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Table(keyspace = "local", name = "book")
public class CBook {

    @PartitionKey
    private Long id;
    private String title;
    private Long price;
}
