package io.penguin.penguincore.http;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SampleRequest {
    private List<Long> ids;
    private String query;
    private String consumer;
}
