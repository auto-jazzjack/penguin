package io.penguin.springboot.starter.yaml;

import io.penguin.springboot.starter.util.CollectionsUtils;

import java.util.Objects;

public class Validator {
    public static void validate(Penguin penguin) {
        Objects.requireNonNull(penguin);
        Objects.requireNonNull(penguin.getKind());
        Objects.requireNonNull(penguin.getVersion());

        validate(penguin.getMetadata());
        validate(penguin.getSpec());
    }

    static void validate(Penguin.Metadata metadata) {
        Objects.requireNonNull(metadata);
        Objects.requireNonNull(metadata.getName());
    }

    static void validate(Penguin.Spec spec) {
        Objects.requireNonNull(spec);
        if (CollectionsUtils.isEmpty(spec.getContainers())) {
            throw new IllegalArgumentException("Spec can not be null" + spec);
        }
        spec.getContainers().forEach(Validator::validate);
    }

    static void validate(Penguin.Container container) {
        Objects.requireNonNull(container);
        Objects.requireNonNull(container.getKind());
        Objects.requireNonNull(container.getName());
    }

}
