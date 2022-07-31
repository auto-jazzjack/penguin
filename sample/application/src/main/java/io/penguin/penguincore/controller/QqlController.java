package io.penguin.penguincore.controller;

import com.example.penguinql.core.ResolverService;
import io.penguin.penguincore.http.SampleRequest;
import io.penguin.penguincore.http.SampleResponse;
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
        return resolverService.exec(request, request.getQuery());
    }


}