package io.penguin.penguincore.plugin.circuit;

import lombok.Builder;
import lombok.Data;

@Data
@Builder

public class CircuitModel {

    int order;
    int failureRateThreshold;
    int waitDurationInOpenStateMillisecond;
    int permittedNumberOfCallsInHalfOpenState;


    public static CircuitModel.CircuitModelBuilder base() {
        return CircuitModel.builder()
                .permittedNumberOfCallsInHalfOpenState(100)
                .failureRateThreshold(10)
                .waitDurationInOpenStateMillisecond(10000);
    }
}
