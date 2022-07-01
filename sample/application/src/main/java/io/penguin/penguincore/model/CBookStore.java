package io.penguin.penguincore.model;

import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Table(keyspace = "local", name = "book_store")
public class CBookStore {

    @PartitionKey
    private Long id;

    private List<Long> book_ids;
    private String contact;
}
