package com.codesoom.assignment.errors.authentication;

public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(String errorMessage) {
        super(errorMessage);
    }
}
