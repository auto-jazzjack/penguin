package com.example.penguinql.exception;

public class NotAuthorizationException extends Throwable {
    public NotAuthorizationException(String message) {
        super(message);
    }

    public NotAuthorizationException() {
    }
}
