package io.penguin.penguincore;

import io.penguin.springboot.starter.config.ReaderTemplateConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@ComponentScan(basePackageClasses = {
        PenguinCoreApplication.class,
        ReaderTemplateConfiguration.class
})
@SpringBootApplication
public class PenguinCoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(PenguinCoreApplication.class, args);
    }

}
