package io.penguin.springboot.starter.controller;

import io.penguin.springboot.starter.Penguin;
import io.penguin.springboot.starter.flow.From;
import io.penguin.springboot.starter.util.IdTypeDetermineUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/v1/debug")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DebugController {

    private final Map<String, Penguin<Object, Object>> deployments;

    @GetMapping(path = "/{key}/{idType}/{id}")
    public Mono<Map<From, Object>> debugKeyAndId(@PathVariable String key, @PathVariable String idType, @PathVariable String id) {
        Penguin<Object, Object> baseDeployment = deployments.get(key);
        if (baseDeployment == null) {
            throw HttpClientErrorException.create(HttpStatus.BAD_REQUEST, "No Such Deployment", new HttpHeaders(), null, StandardCharsets.UTF_8);
        }

        return baseDeployment.debugOne(IdTypeDetermineUtil.getConverter(idType).apply(id));
    }
}
