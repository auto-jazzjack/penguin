package io.penguin.penguincore.plugin.timeout;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TimeoutModel {

    long timeoutMilliseconds;

    public static TimeoutModel.TimeoutModelBuilder base() {
        return TimeoutModel.builder()
                .timeoutMilliseconds(SECOND * 10);
    }

    private static final long SECOND = 1000;
}
