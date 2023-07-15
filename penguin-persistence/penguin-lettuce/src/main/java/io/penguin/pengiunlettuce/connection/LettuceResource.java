package io.penguin.pengiunlettuce.connection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LettuceResource {
    private Integer port;
    private String redisUris;
}
