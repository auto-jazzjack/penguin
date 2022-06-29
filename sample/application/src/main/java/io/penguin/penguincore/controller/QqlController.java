package io.penguin.penguincore.controller;

import io.penguin.penguincore.http.SampleRequest;
import io.penguin.penguincore.http.SampleResponse;
import io.penguin.penguincore.reader.FirstExample;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RestController
public class QqlController {

    @Autowired
    FirstExample firstExample;

    @PostMapping(path = "/hello")
    public Mono<SampleResponse> request(@RequestBody SampleRequest request) {
        return Mono.empty();
    }


}