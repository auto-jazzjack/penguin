package io.penguin.penguinkafka.reader;

public interface KafkaReader<K, V> {

    void consume();

    void action(K key, V value);

    V deserialize(byte[] bytes);
}
