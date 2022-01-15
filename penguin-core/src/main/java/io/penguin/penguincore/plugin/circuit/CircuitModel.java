package io.penguin.penguincore.plugin.circuit;

import lombok.Builder;
import lombok.Data;

import java.time.Duration;

@Data
@Builder
public class CircuitModel {

    int order;
    int failureRateThreshold;
    int waitDurationInOpenStateMillisecond;
    int permittedNumberOfCallsInHalfOpenState;
}
