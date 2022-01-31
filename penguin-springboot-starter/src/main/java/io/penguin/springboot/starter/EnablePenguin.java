package io.penguin.springboot.starter;

import io.penguin.springboot.starter.repository.configuration.EnablePenguinRepositories;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({EnablePenguinRepositories.class, PenguinConfiguration.class})
public @interface EnablePenguin {
}
