package io.penguin.penguincore.reader;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BaseOverWriteReader<K, I, V> implements Reader<K, I> {

    public BaseOverWriteReader() {
    }

    abstract public void merge(V agg, I inner);
}
