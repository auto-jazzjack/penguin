package com.example.penguinql.acl.exception;

public class NotAuthorizationException extends Throwable {
    public NotAuthorizationException(String message) {
        super(message);
    }

    public NotAuthorizationException() {
    }
}
