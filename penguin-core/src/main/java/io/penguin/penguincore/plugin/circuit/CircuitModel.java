package io.penguin.penguincore.plugin.circuit;

import lombok.Builder;
import lombok.Data;

@Data
@Builder

public class CircuitModel {

    int order;
    float failureRateThreshold;
    int waitDurationInOpenStateMillisecond;
    int permittedNumberOfCallsInHalfOpenState;


    public static CircuitModel.CircuitModelBuilder base() {
        return CircuitModel.builder()
                .order(0)
                .permittedNumberOfCallsInHalfOpenState(100)
                .failureRateThreshold(0.1F)
                .waitDurationInOpenStateMillisecond(10000);
    }
}
