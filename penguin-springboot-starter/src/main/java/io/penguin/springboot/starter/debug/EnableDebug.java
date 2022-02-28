package io.penguin.springboot.starter.debug;

import io.penguin.springboot.starter.config.BaseDeploymentBean;
import io.penguin.springboot.starter.controller.DebugController;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({DebugController.class, BaseDeploymentBean.class})
public @interface EnableDebug {
}
