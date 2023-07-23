package io.penguin.penguincore.util;

import io.micrometer.core.instrument.Timer;
import io.penguin.penguincore.reader.Reader;
import io.penguin.penguincore.util.MetricReader;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

public class MetricReaderTest {


    @Test
    public void asd() {
        MetricReader<String, String> hello = new MetricReader<>(new Reader<>() {
            @Override
            public Mono<String> findOne(String key) {
                try {
                    Thread.sleep(1000);
                    return Mono.just(key);
                } catch (Exception e) {
                    return Mono.just("123");
                }
            }
        });
        Mono<String> one = hello.findOne("111");
        one.block();

        Timer t = hello.timer;
    }
}
