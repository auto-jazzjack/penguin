package com.example.penguinql.core.prune;

public interface GeneralFieldMethod<P, M> {
    void setData(P parent, M myself);

    M getData(P parent);

    M baseInstance();
}
