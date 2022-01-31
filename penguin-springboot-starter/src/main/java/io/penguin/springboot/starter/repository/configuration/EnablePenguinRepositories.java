package io.penguin.springboot.starter.repository.configuration;

import io.penguin.springboot.starter.config.PenguinConfig;
import io.penguin.springboot.starter.repository.support.PenguinRepositoryFactoryBean;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({PenguinRepositoriesRegistrar.class, PenguinConfig.class})
public @interface EnablePenguinRepositories {

    String[] value() default {};

    String[] basePackages() default {};

    Class<?>[] basePackageClasses() default {};

    Filter[] excludeFilters() default {};

    Filter[] includeFilters() default {};


    Class<?> repositoryFactoryBeanClass() default PenguinRepositoryFactoryBean.class;

}
