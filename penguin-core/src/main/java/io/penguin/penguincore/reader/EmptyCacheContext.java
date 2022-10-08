package io.penguin.penguincore.reader;

public class EmptyCacheContext<V> implements CacheContext<V> {

    public EmptyCacheContext() {
    }

    @Override
    public V getValue() {
        return null;
    }

    @Override
    public long getTimeStamp() {
        return 0;
    }
}
