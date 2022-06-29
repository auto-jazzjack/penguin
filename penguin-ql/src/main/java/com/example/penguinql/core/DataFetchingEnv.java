package com.example.penguinql.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataFetchingEnv {

    private KeyValue<Object, Object> root;
    private KeyValue<Object, Object> nearRoot;
    private Context context;


    public DataFetchingEnv setContext(Context context) {
        this.context = context;
        return this;
    }

    public DataFetchingEnv setRoot(KeyValue<Object, Object> root) {
        this.root = root;
        return this;
    }

    public DataFetchingEnv setNearRoot(KeyValue<Object, Object> nearRoot) {
        this.nearRoot = nearRoot;
        return this;
    }
}
