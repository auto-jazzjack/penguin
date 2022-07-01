package io.penguin.penguincore.http;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SampleResponse {
    private List<BookStore> bookStores;
}
