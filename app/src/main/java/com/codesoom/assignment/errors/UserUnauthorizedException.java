package com.codesoom.assignment.errors;

public class UserUnauthorizedException extends RuntimeException {
    public UserUnauthorizedException() {
        super("You are not authorized.");
    }
}
