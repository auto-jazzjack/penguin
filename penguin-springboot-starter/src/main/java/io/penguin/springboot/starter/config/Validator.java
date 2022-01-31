package io.penguin.springboot.starter.config;

import io.penguin.springboot.starter.util.CollectionsUtils;

import java.util.Objects;

public class Validator {
    public static void validate(PenguinConfig penguinConfig) {
        Objects.requireNonNull(penguinConfig);
        Objects.requireNonNull(penguinConfig.getKind());
        Objects.requireNonNull(penguinConfig.getVersion());

        validate(penguinConfig.getMetadata());
        validate(penguinConfig.getSpec());
    }

    static void validate(PenguinConfig.Metadata metadata) {
        Objects.requireNonNull(metadata);
        Objects.requireNonNull(metadata.getName());
    }

    static void validate(PenguinConfig.Spec spec) {
        Objects.requireNonNull(spec);
        if (CollectionsUtils.isEmpty(spec.getContainers())) {
            throw new IllegalArgumentException("Spec can not be null" + spec);
        }
        spec.getContainers().forEach(Validator::validate);
    }

    static void validate(PenguinConfig.Container container) {
        Objects.requireNonNull(container);
        Objects.requireNonNull(container.getKind());
        Objects.requireNonNull(container.getName());
    }

}
