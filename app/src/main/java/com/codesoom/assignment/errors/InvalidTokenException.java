package com.codesoom.assignment.errors;

/**
 * 액세스 토큰이 유효하지않은경우 던집니다.
 */
public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(String token) {
        super("Invalid token: " + token);
    }
}
