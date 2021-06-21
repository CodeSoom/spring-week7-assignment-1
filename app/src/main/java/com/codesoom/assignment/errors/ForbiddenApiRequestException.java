package com.codesoom.assignment.errors;

public class ForbiddenApiRequestException extends RuntimeException {
    public ForbiddenApiRequestException(String message) {
        super(message);
    }
}
