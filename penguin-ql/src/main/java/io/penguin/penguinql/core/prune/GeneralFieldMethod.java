package io.penguin.penguinql.core.prune;

public interface GeneralFieldMethod<P, M> {
    void setData(P parent, M myself);

    M getData(P parent);

    M baseInstance();
}
