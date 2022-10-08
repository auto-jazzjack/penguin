package io.penguin.penguincore.writer;

public interface Writer<K, V> {
    void writeOne(K key, V value);

    void writeOneLazy(K key, V value);
}
