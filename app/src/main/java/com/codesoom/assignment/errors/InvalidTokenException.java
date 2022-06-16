package com.codesoom.assignment.errors;

public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(String token) {
        super("Invalid token: " + token);
    }

    public InvalidTokenException(String token, Throwable cause) {
        super("Invalid token: " + token, cause);
    }
}
