package io.penguin.penguinkafka.reader;

import io.penguin.penguinkafka.model.KafkaProps;
import io.penguin.penguinkafka.util.KafkaUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndTimestamp;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
public abstract class KafkaProcessor<K, V> implements KafkaReader<K, V> {

    private final KafkaConsumer<K, byte[]> consumer;
    //Value is meaning less. Just used ConsurrentMap to support atomicity.
    private final ConcurrentMap<TopicPartition, Object> currentPartition;
    private final long poll;
    private final AtomicInteger concurrency;
    private final AtomicInteger targetConcurrency;

    public KafkaProcessor(KafkaProps kafkaProps) {
        Properties props = new Properties();
        props.put("bootstrap.servers", kafkaProps.getBootStrap());
        props.put("key.deserializer", kafkaProps.getKeySerializer().getName());
        props.put("value.deserializer", ByteArrayDeserializer.class.getName());
        props.put("group.id", kafkaProps.getGroupId());
        poll = kafkaProps.getPoll() == null ? 10000 : kafkaProps.getPoll();
        targetConcurrency = new AtomicInteger(kafkaProps.getConcurrency());
        concurrency = new AtomicInteger(0);

        currentPartition = new ConcurrentHashMap<>();

        consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singleton(kafkaProps.getTopic()), new ConsumerRebalanceListener() {
            @Override
            public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
                log.info("This is revoked partition {}", partitions);
                partitions.forEach(currentPartition::remove);
            }

            @Override
            public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
                log.info("This is assigned partition {}", partitions);
                currentPartition.putAll(partitions.stream().collect(Collectors.toMap(i -> i, i -> new Object())));
            }
        });
    }

    /**
     * Return the number of revived
     */
    @Override
    public int consume() {
        if (concurrency.get() >= targetConcurrency.get()) {
            return 0;
        }

        int targetCnt = targetConcurrency.get() - concurrency.get();
        ExecutorService executorService = Executors.newFixedThreadPool(targetCnt);

        for (int j = 0; j < targetCnt; j++) {
            executorService.submit(() -> {
                try {
                    concurrency.getAndIncrement();
                    while (true) {
                        ConsumerRecords<K, byte[]> consumerRecords = consumer.poll(poll);
                        KafkaUtil.aggregate(consumerRecords, this::deserialize)
                                .forEach(i -> action(i.getKey(), i.getValue()));
                        consumer.commitSync();
                    }
                } catch (Exception e) {
                    log.error("");
                } finally {
                    executorService.shutdown();
                    concurrency.decrementAndGet();
                    currentPartition.clear();
                    consumer.close();
                }
            });
        }
        return targetCnt;
    }

    public Map<TopicPartition, OffsetAndTimestamp> rewind(Date date) {

        long time = date.getTime();

        Map<TopicPartition, Long> collect = Optional.ofNullable(currentPartition)
                .orElse(new ConcurrentHashMap<>())
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, i -> time));

        //Actually 1 second is magic number. But it is enough number timeout
        Map<TopicPartition, OffsetAndTimestamp> topics = consumer.offsetsForTimes(collect, Duration.ofMillis(1000));
        Map<TopicPartition, Long> partitionOffsetMap = topics.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, i -> i.getValue().offset()));

        return consumer.offsetsForTimes(partitionOffsetMap, Duration.ofMillis(1000));

    }
}
