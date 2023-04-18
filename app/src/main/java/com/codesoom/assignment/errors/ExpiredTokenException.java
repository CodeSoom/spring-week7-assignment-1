package com.codesoom.assignment.errors;

public class ExpiredTokenException extends RuntimeException {

    public ExpiredTokenException(String token) {
        super("Expired token: " + token);
    }
}
