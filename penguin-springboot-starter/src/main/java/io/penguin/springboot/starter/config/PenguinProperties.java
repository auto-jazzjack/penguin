package io.penguin.springboot.starter.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;


@Data
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "penguin")
public class PenguinProperties {
    private String version;
    private String kind;

    @NestedConfigurationProperty
    private Metadata metadata;

    @NestedConfigurationProperty
    private Spec spec;


    @Data
    public static class Metadata {
        private String name;
    }

    @Data
    public static class Spec {
        private List<Worker> workers;
        private List<Resource> resources;
    }

    @Data
    public static class Resource {
        private String name;
        private Map<String, Object> spec;
    }

    @Data
    public static class Worker {
        private List<Container> containers;
        private Class<?> aggregatedTarget;
        private String name;
        private String kind;
    }

    @Data
    public static class Container {
        private String name;
        private String kind;

        @NestedConfigurationProperty
        private Map<String, Object> spec;
    }

}
