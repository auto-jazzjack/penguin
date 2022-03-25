package io.penguin.penguincore;

import io.penguin.springboot.starter.debug.EnableDebug;
import io.penguin.springboot.starter.debug.EnableRefresh;
import io.penguin.springboot.starter.repository.configuration.EnablePenguinRepositories;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
@EnablePenguinRepositories
@EnableDebug
@EnableRefresh
public class PenguinCoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(PenguinCoreApplication.class, args);
    }

}
