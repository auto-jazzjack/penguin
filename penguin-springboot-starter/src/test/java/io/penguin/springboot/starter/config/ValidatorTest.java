package io.penguin.springboot.starter.config;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ValidatorTest {

    @Test
    public void should_properties_check_failed() {
        PenguinProperties penguinProperties = new PenguinProperties();
        Assertions.assertThrows(NullPointerException.class, () -> Validator.validate(penguinProperties));
    }

    @Test
    public void should_properties_check_failed2() {
        PenguinProperties penguinProperties = new PenguinProperties();
        penguinProperties.setVersion("Hello");
        Assertions.assertThrows(NullPointerException.class, () -> Validator.validate(penguinProperties));
    }

    @Test
    public void should_properties_check_failed3() {
        PenguinProperties penguinProperties = new PenguinProperties();
        penguinProperties.setVersion("Hello");
        PenguinProperties.Metadata metadata = new PenguinProperties.Metadata();
        metadata.setName("hello");
        penguinProperties.setMetadata(metadata);
        Assertions.assertThrows(NullPointerException.class, () -> Validator.validate(new PenguinProperties()));
    }
}
