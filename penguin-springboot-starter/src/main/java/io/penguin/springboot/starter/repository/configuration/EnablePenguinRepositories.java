package io.penguin.springboot.starter.repository.configuration;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({PenguinRepositoriesRegistrar.class})
public @interface EnablePenguinRepositories {


}
