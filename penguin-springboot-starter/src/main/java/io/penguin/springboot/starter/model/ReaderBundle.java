package io.penguin.springboot.starter.model;

import io.penguin.penguincore.reader.CacheContext;
import io.penguin.penguincore.reader.Reader;
import io.penguin.springboot.starter.mapper.ContainerKind;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReaderBundle<K, V> {
    private Reader<K, CacheContext<V>> reader;
    private ContainerKind kind;
}
