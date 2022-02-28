package io.penguin.springboot.starter.controller;

import io.penguin.springboot.starter.kind.BaseDeployment;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@RestController("/api/v1")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UpdateController {

    private final Map<String, BaseDeployment<Object, Object>> deployments;
    private final String OK = "OK";


    @PutMapping("/refresh/{key}/{id}")
    public String refreshByKeyAndId(@PathVariable("key") String key, @PathVariable("id") String id) throws Exception {
        BaseDeployment<Object, Object> baseDeployment = deployments.get(key);
        if (baseDeployment == null) {
            throw HttpClientErrorException.create(HttpStatus.BAD_REQUEST, "No Such Deployment", new HttpHeaders(), null, StandardCharsets.UTF_8);
        }

        baseDeployment.getRemoteCache().insertQueue(id);
        return OK;
    }
}
