package com.example.penguinql.core.setter;

public interface GeneralFieldMethod<P, M> {
    void setData(P parent, M myself);

    M getData(P parent);

    M defaultInstance();
}
