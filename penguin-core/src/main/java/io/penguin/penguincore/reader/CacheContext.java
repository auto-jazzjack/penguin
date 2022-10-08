package io.penguin.penguincore.reader;

public interface CacheContext<V> {
    V getValue();

    long getTimeStamp();
}
