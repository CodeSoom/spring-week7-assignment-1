package com.codesoom.assignment.errors;

public class UserForbiddenException extends RuntimeException {
    public UserForbiddenException() {
        super("You are not authorized.");
    }
}
