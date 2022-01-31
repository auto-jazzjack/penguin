package io.penguin.springboot.starter;

import io.penguin.springboot.starter.config.PenguinConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;


@Configuration
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PenguinConfiguration {
    private final PenguinConfig penguinConfig;

    @PostConstruct
    public void init(){
        System.out.println(penguinConfig);
        System.out.println("hello");
    }
}
