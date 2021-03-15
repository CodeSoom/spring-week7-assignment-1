package com.codesoom.assignment.errors;

/**
 * 잘못된 Token 예외.
 */
public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(String token) {
        super("Invalid token: " + token);
    }
}
