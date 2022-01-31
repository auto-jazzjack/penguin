package io.penguin.penguincore;

import io.penguin.springboot.starter.EnablePenguin;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
@EnablePenguin
public class PenguinCoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(PenguinCoreApplication.class, args);
    }

}
