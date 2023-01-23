package io.penguin.penguincore.controller;

import io.penguin.penguinql.core.ResolverService;
import io.penguin.penguinql.exception.NotAuthorizationException;
import io.penguin.penguincore.http.SampleRequest;
import io.penguin.penguincore.http.SampleResponse;
import io.penguin.springboot.starter.exception.BadRequest;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class QqlController {

    private final ResolverService<SampleRequest, SampleResponse> resolverService;

    @PostMapping(path = "/hello", consumes = "application/json", produces = "application/json")
    public Mono<SampleResponse> request(@RequestBody SampleRequest request) throws Exception {
        return resolverService.exec(request, request.getConsumer(), request.getQuery())
                .onErrorResume(e -> {
                    if (e instanceof NotAuthorizationException) {
                        return Mono.error(new BadRequest(e.getMessage()));
                    }
                    return Mono.error(e);
                });
    }


}