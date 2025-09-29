package com.example.tasktrackerapi.exeption;

public class AuthorizationFailedException extends RuntimeException {
    public AuthorizationFailedException() {}
    public AuthorizationFailedException(String message) {
        super(message);
    }
}
