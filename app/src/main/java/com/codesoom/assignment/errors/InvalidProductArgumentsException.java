package com.codesoom.assignment.errors;

public class InvalidProductArgumentsException extends RuntimeException {

    public InvalidProductArgumentsException(String message) {
        super(message);
    }
}
