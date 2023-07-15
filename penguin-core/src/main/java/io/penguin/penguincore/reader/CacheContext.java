package io.penguin.penguincore.reader;

public interface CacheContext<V> {

    long getTimeStamp();

    V getValue();
}
