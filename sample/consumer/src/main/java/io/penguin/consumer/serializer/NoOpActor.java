package io.penguin.consumer.serializer;

import io.penguin.penguinkafka.model.Actor;


public class NoOpActor implements Actor<String, String> {
    @Override
    public void action(String key, String value) {
        System.out.println(key + " " + value);
    }

    @Override
    public String deserialize(byte[] bytes) {
        return new String(bytes);
    }
}
