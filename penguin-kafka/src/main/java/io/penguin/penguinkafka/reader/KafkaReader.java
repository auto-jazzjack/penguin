package io.penguin.penguinkafka.reader;

public interface KafkaReader<K, V> {

    int consume();

    void action(K key, V value);

    V deserialize(byte[] bytes);
}
