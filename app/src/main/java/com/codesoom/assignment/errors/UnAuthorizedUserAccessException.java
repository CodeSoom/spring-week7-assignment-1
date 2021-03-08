package com.codesoom.assignment.errors;

public class UnAuthorizedUserAccessException extends RuntimeException {
    public UnAuthorizedUserAccessException() {
        super("Unauthorized user access resources");
    }
}
