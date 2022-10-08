package io.penguin.penguincore.writer;

public interface Writer<K, V> {
    void writeOne(K key, V value);

    long expireSecond();

    String prefix();
}
