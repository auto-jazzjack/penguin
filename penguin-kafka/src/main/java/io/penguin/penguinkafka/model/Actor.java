package io.penguin.penguinkafka.model;

public interface Actor<K, V> {

    void action(K key, V value);

    V deserialize(byte[] bytes);
}
