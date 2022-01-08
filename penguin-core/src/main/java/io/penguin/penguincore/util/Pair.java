package io.penguin.penguincore.util;

import java.util.Map;

public class Pair<K, V> implements Map.Entry<K, V> {
    private final K k;
    private final V v;

    public static <_K, _V> Pair<_K, _V> of(_K k, _V v) {
        return new Pair<>(k, v);
    }

    public Pair(K k, V v) {
        this.k = k;
        this.v = v;
    }

    @Override
    public K getKey() {
        return k;
    }

    @Override
    public V getValue() {
        return v;
    }

    @Override
    public V setValue(V value) {
        throw new IllegalStateException("Changing value of Pair is not supported");
    }
}
