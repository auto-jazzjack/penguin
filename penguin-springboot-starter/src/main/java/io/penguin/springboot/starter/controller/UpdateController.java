package io.penguin.springboot.starter.controller;

import io.penguin.springboot.starter.Penguin;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/v1/refresh")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UpdateController {

    private final Map<String, Penguin<Object, Object>> deployments;
    private final String OK = "OK";


    @PutMapping(path = "/{key}/{id}")
    public String refreshByKeyAndId(@PathVariable("key") String key, @PathVariable("id") String id) throws Exception {
        Penguin<Object, Object> baseDeployment = deployments.get(key);
        if (baseDeployment == null) {
            throw HttpClientErrorException.create(HttpStatus.BAD_REQUEST, "No Such Deployment", new HttpHeaders(), null, StandardCharsets.UTF_8);
        }

        baseDeployment.refreshOne(id);
        return OK;
    }
}
