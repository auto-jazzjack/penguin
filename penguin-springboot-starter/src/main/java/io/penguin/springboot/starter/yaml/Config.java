package io.penguin.springboot.starter.yaml;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.annotation.PostConstruct;

@Import(Penguin.class)
@Configuration
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class Config {

    private final Penguin penguin;

    @PostConstruct
    public void init(){
        System.out.println(penguin);
        Validator.validate(penguin);
    }
}
