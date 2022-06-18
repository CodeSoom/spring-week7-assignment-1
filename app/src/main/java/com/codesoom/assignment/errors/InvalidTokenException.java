package com.codesoom.assignment.errors;

/**
 * 유효하지 않은 토큰에 대한 예외 클래스
 */
public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(String token) {
        super("Invalid token: " + token);
    }
}
