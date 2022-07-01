package io.penguin.penguincore.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RestController
public class TestController {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ExecutorService executorService = Executors.newFixedThreadPool(2000);


    /*@PostMapping(path = "/hello")
    public Mono<Map<String, String>> read() {
        return firstExample.findOne(123L);
    }*/


    @GetMapping(path = "/stress")
    public String stress() throws Exception {
        stressor();
        return "hello";
    }

    private void stressor() throws Exception {

        int size = 50000;
        CountDownLatch countDownLatch = new CountDownLatch(size);
        for (int i = 0; i < size; i++) {
            if (i % 100 == 0) {
                log.info("" + i);
            }
            try {
                executorService.submit(() -> {
                    try {
                        restTemplate
                                .postForEntity(URI.create("http://localhost:9876/hello"), null, Map.class);
                    } catch (Exception e) {
                        //System.out.println(e);
                    }
                    countDownLatch.countDown();
                });
            } catch (Exception e) {
                //System.out.println();
            }
        }


        boolean await = countDownLatch.await(15, TimeUnit.SECONDS);
        //countDownLatch.await();
        log.info("" + await);
        System.out.println(countDownLatch.getCount());
    }

}