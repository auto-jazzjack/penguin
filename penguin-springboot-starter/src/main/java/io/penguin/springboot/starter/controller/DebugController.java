package io.penguin.springboot.starter.controller;

import io.penguin.springboot.starter.flow.From;
import io.penguin.springboot.starter.kind.BaseDeployment;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@RestController("/api/v1")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DebugController {

    private final Map<String, BaseDeployment<Object, Object>> deployments;

    @GetMapping("/debug/{key}/{id}")
    public Mono<Map<From, Object>> debugKeyAndId(@PathVariable("key") String key, @PathVariable("id") String id) throws Exception {
        BaseDeployment<Object, Object> baseDeployment = deployments.get(key);
        if (baseDeployment == null) {
            throw HttpClientErrorException.create(HttpStatus.BAD_REQUEST, "No Such Deployment", new HttpHeaders(), null, StandardCharsets.UTF_8);
        }

        return baseDeployment.debugOne(id);
    }
}
