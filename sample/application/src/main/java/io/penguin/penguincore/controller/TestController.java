package io.penguin.penguincore.controller;

import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.penguin.deployment.penguindeployment.Deployment;
import io.penguin.pengiunlettuce.LettuceCacheConfig;
import io.penguin.penguincore.reader.RedisCache;
import io.penguin.penguincore.reader.Source;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.Map;

@Component
@RestController
public class TestController {


    private Deployment<String, Map<String, String>> deployment;

    @Autowired
    StatefulRedisClusterConnection<String, byte[]> statefulRedisClusterConnection;

    @PostConstruct
    public void init() {
        Source source = new Source();

        deployment = new Deployment(
                new RedisCache(source, statefulRedisClusterConnection, LettuceCacheConfig.builder()
                        .build()),
                source

        );
    }

    @PostMapping(path = "/hello")
    public Mono<Map<String, String>> read() {
        return deployment.findOne("ad");
    }
}