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
                .permittedNumberOfCallsInHalfOpenState(5)
                .failureRateThreshold(50f)
                .circuitName("")
                .waitDurationInOpenStateMillisecond(1000);
    }
}
