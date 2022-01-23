package io.penguin.penguincore.plugin.bulkhead;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BulkheadModel {

    private BulkHeadMode bulkHeadMode;
    private String bulkheadName;
    private int maxConcurrentCalls;
    private long maxWaitDurationMilliseconds;

    //current version only support SEMAPHORE
    public enum BulkHeadMode {
        //THREAD,
        SEMAPHORE
    }

    public static BulkheadModel.BulkheadModelBuilder base() {
        return BulkheadModel.builder()
                .bulkHeadMode(BulkHeadMode.SEMAPHORE)
                .maxWaitDurationMilliseconds(500)
                .maxConcurrentCalls(300);
    }
}
