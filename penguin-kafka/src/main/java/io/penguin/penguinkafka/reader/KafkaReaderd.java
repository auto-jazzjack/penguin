package io.penguin.penguinkafka.reader;

public class KafkaReaderd implements KafkaReader{



    @Override
    public void consume() {
        try {
            while (true) {
                // 계속 loop를 돌면서 producer의 message를 띄운다.
                ConsumerRecords<String, String> records = consumer.poll(100);
                for (ConsumerRecord<String, String> record : records)
                    System.out.println(record.value());
            }
        } catch (Exception e) {
        } finally {
            consumer.close();
        }
    }


    public static void asd(String[] args) {
        Properties props = new Properties();

        // kafka server host 및 port 설정
        props.put("bootstrap.servers", KAFKA_SINGLE_IP);
        props.put("group.id", "karim-group-id-1"); // group-id 설정
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer"); // key deserializer
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer"); // value deserializer

        // consumer 생성
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        // topic 설정
        consumer.subscribe(Collections.singletonList(TOPIC_NAME));


    }
}
