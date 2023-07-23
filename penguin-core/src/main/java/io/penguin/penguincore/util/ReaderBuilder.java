package io.penguin.penguincore.util;

import io.penguin.penguincore.reader.Reader;

public class ReaderBuilder {

    public static <K, V> Reader<K, V> build(ReaderConfig<K, V> readerConfig) {

        ReaderDelegate<K, V> reader = new ReaderDelegate<>(
                readerConfig.getCaller(), readerConfig.getFallback(), readerConfig.getValueType()
        );

        if (readerConfig.getBulkheadModel() != null) {
            reader.addBulkHead(readerConfig.getBulkheadModel());
        }
        if (readerConfig.getTimeoutModel() != null) {
            reader.addTimeout(readerConfig.getTimeoutModel());
        }
        if (readerConfig.getCircuitModel() != null) {
            reader.addCircuit(readerConfig.getCircuitModel());
        }

        return reader;
    }

}
