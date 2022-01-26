package io.penguin.springboot.starter.yaml;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;


@Data
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "penguin")
public class Penguin {
    private String version;
    private String kind;
    private Metadata metadata;
    private Spec spec;


    @Data
    public static class Metadata {
        private String name;
    }

    @Data
    public static class Spec {
        private List<Container> containers;
    }

    @Data
    public static class Container {
        private String name;
        private Class<?> clazz;
        private Map<String, Object> spec;
    }

}
