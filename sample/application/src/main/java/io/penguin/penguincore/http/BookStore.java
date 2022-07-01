package io.penguin.penguincore.http;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class BookStore {

    private Long id;
    private List<Book> books;
    private String contact;
}
