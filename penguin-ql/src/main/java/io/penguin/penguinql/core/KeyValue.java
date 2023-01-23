package io.penguin.penguinql.core;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class KeyValue<K, V> {
    private K key;
    private V value;
    //private Resolver.DataType type;

    public static <K_, V_> KeyValue<K_, V_> of(K_ key, V_ value/*, Resolver.DataType type*/) {
        return KeyValue.<K_, V_>builder()
                .key(key)
                .value(value)
                .build();
    }

    public Object getValueByKey() {
        if (key == null && value == null) {
            return null;
        }
        if (value instanceof List) {
            return ((List<?>) value).get((Integer) key);
        } else if (value instanceof Map) {
            return ((Map<?, ?>) value).get(key);
        } else {
            return value;
        }
    }
}