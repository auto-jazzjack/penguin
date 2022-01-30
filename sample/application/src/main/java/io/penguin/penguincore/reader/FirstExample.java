package io.penguin.penguincore.reader;


import io.penguin.springboot.starter.Deployment;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public interface FirstExample extends Deployment<String, Map<String, String>> {

}
