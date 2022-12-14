package com.example.penguinql.exception;

public class InvalidQueryException extends Throwable {
    public InvalidQueryException() {
    }

    public InvalidQueryException(String message) {
        super(message);
    }
}
