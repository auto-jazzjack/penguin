package io.penguin.springboot.starter.config;

import io.penguin.springboot.starter.util.CollectionsUtils;

import java.util.Objects;

public class Validator {
    public static void validate(PenguinProperties penguinProperties) {
        Objects.requireNonNull(penguinProperties);
        //Objects.requireNonNull(penguinProperties.getKind());
        Objects.requireNonNull(penguinProperties.getVersion());

        validate(penguinProperties.getMetadata());
        validate(penguinProperties.getSpec());
    }

    static void validate(PenguinProperties.Metadata metadata) {
        Objects.requireNonNull(metadata);
        Objects.requireNonNull(metadata.getName());
    }

    static void validate(PenguinProperties.Spec spec) {
        Objects.requireNonNull(spec);
        if (CollectionsUtils.isEmpty(spec.getWorkers())) {
            throw new IllegalArgumentException("Spec can not be null" + spec);
        }
        spec.getWorkers().forEach(Validator::validate);
    }

    static void validate(PenguinProperties.Worker worker) {
        Objects.requireNonNull(worker);
        Objects.requireNonNull(worker.getName());
        Objects.requireNonNull(worker.getAggregatedTarget());
        Objects.requireNonNull(worker.getContainers());
        Objects.requireNonNull(worker.getKind());
        worker.getContainers().forEach(Validator::validate);
    }

    static void validate(PenguinProperties.Container container) {
        Objects.requireNonNull(container);
        Objects.requireNonNull(container.getKind());
        Objects.requireNonNull(container.getName());
    }

}
