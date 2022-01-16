package io.penguin.penguincore.plugin.timeout;

import lombok.Builder;
import lombok.Data;

@Data
@Builder

public class TimeoutModel {

    int order;
    long timeoutMilliseconds;


    public static TimeoutModel.TimeoutModelBuilder base() {
        return TimeoutModel.builder()
                .order(Integer.MIN_VALUE)
                .timeoutMilliseconds(SECOND * 10);
    }

    private static final long SECOND = 1000;
}
