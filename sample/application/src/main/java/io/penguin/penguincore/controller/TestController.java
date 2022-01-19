package io.penguin.penguincore.controller;

import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.penguin.deployment.penguindeployment.Deployment;
import io.penguin.pengiunlettuce.LettuceCacheConfig;
import io.penguin.penguincore.plugin.PluginInput;
import io.penguin.penguincore.plugin.timeout.TimeoutModel;
import io.penguin.penguincore.reader.ObjectMapperCache;
import io.penguin.penguincore.reader.Source;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
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
    private final ExecutorService executorService = Executors.newFixedThreadPool(200);
    private Deployment<String, Map<String, String>> deployment;

    @Autowired
    StatefulRedisClusterConnection<String, byte[]> connection;

    @PostConstruct
    public void init() throws Exception {
        Source source = new Source();

        deployment = new Deployment(
                new ObjectMapperCache(source, connection, LettuceCacheConfig.base()
                        .pluginInput(PluginInput.base()
                                .timeout(TimeoutModel.base()
                                        .timeoutMilliseconds(100)
                                        .build())
                                .build())
                        .build()),
                source

        );
    }

    @PostMapping(path = "/hello")
    public Mono<Map<String, String>> read() {
        return deployment.findOne("ad");
    }


    @GetMapping(path = "/stress")
    public String stress() throws Exception {
        stressor();
        return "hello";
    }

    private void stressor() throws Exception {

        int size = 10000;
        CountDownLatch countDownLatch = new CountDownLatch(size);
        for (int i = 0; i < size; i++) {
            if (i % 100 == 0) {
                log.info("" + i);
            }
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        restTemplate.postForEntity(URI.create("http://localhost:9876/hello"), null, Map.class);
                        countDownLatch.countDown();
                    } catch (Exception e) {
                        log.error("", e);
                        countDownLatch.countDown();
                    }

                }
            }).get();
        }
        boolean await = countDownLatch.await(100, TimeUnit.SECONDS);
        log.info("" + await);
        System.out.println(countDownLatch.getCount());
    }

}