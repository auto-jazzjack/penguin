package io.penguin.penguincore.writer;

public interface Writer<V> {
    void writeOne(String key, V value);
    long expireSecond();
    String prefix();
}
