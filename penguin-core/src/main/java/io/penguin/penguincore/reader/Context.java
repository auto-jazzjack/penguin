package io.penguin.penguincore.reader;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Context<V> {
    private V value;
    private From from;


    public enum From {
        SOURCE,
        CACHE1,
    }
}
