package io.penguin.penguincore.http;

import io.penguin.penguincore.model.BookStore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SampleResponse {
    private List<BookStore> bookStores;
}
