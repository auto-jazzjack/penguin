package io.penguin.penguincore;

import io.penguin.springboot.starter.repository.configuration.EnablePenguinRepositories;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@EnablePenguinRepositories
@SpringBootApplication
public class PenguinCoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(PenguinCoreApplication.class, args);
    }

}
