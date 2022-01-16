package io.penguin.penguincore.plugin.circuit;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CircuitModel {

    private float failureRateThreshold;
    private int waitDurationInOpenStateMillisecond;
    private int permittedNumberOfCallsInHalfOpenState;
    private String circuitName;


    public static CircuitModel.CircuitModelBuilder base() {
        return CircuitModel.builder()
                .permittedNumberOfCallsInHalfOpenState(100)
                .failureRateThreshold(0.1F)
                .circuitName("")
                .waitDurationInOpenStateMillisecond(10000);
    }
}
