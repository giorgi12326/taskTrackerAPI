package com.example.tasktrackerapi.exeption;


public class AuthenticationFailedException extends RuntimeException {
    public AuthenticationFailedException() {
        super("Authentification failed");
    }
    public AuthenticationFailedException(String message) {
        super(message);
    }
}