package io.penguin.penguincore.model;

import com.datastax.driver.mapping.annotations.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "first_table", keyspace = "local")
public class MyModel {
    private Long id;
    private Long first;
}
