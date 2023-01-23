package io.penguin.penguinql.util;

public class Pair<K, V> {
    private final K k;
    private final V v;

    public static <_K, _V> Pair<_K, _V> of(_K k, _V v) {
        return new Pair<>(k, v);
    }

    public Pair(K k, V v) {
        this.k = k;
        this.v = v;
    }


    public K getKey() {
        return k;
    }

    public V getValue() {
        return v;
    }
}
