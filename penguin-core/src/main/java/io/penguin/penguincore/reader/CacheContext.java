package io.penguin.penguincore.reader;

public interface CacheContext {
    byte[] getValue();

    long getTimeStamp();
}
