package io.penguin.penguincore.reader;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Sinks;

public class DefaultCacheReaderImplTest {


    @Test
    public void asd() throws Exception {
        Sinks.Many<Object> objectMany = Sinks.many().unicast()
                .onBackpressureBuffer();
        objectMany.asFlux()
                .subscribe(i -> System.out.println(i));

        for (int i = 0; i < 100; i++)
            objectMany.tryEmitNext(i);

        Thread.sleep(111);

        for (int i = 100; i < 110; i++)
            objectMany.tryEmitNext(i);
    }
}
