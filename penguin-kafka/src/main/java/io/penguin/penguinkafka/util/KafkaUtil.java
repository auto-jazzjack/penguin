package io.penguin.penguinkafka.util;

import io.penguin.penguinkafka.model.Actor;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class KafkaUtil {
    public static <K, V> List<Pair<K, V>> aggregate(ConsumerRecords<K, byte[]> consumerRecords, Function<byte[], V> deserializer) {
        if (consumerRecords == null) {
            return Collections.emptyList();
        } else {
            List<Pair<K, V>> retv = new ArrayList<>();
            for (ConsumerRecord<K, byte[]> record : consumerRecords) {
                retv.add(Pair.of(record.key(), deserializer.apply(record.value())));
            }
            return retv;
        }
    }

    public static Actor createActor(Class<? extends Actor> clazz) {
        try {
            Constructor<? extends Actor> constructor = clazz.getConstructor();
            Actor actor = constructor.newInstance();
            return actor;
        } catch (Exception e) {
            throw new RuntimeException("Cannot craete actor");
        }
    }
}
