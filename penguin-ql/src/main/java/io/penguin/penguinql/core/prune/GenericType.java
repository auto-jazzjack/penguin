package io.penguin.penguinql.core.prune;

public enum GenericType {
    MAP,
    SET,
    LIST,
    NONE;

    public boolean isCollection() {
        return !this.equals(NONE);
    }
}