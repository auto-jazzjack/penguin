package io.penguin.penguinql.exception;

public class InvalidQueryException extends Throwable {
    public InvalidQueryException() {
    }

    public InvalidQueryException(String message) {
        super(message);
    }
}
