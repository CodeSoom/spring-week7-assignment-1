package com.codesoom.assignment.errors;

public class UnAuthorizationException extends RuntimeException {
    public static final String DEFAULT_MESSAGE = "접근 권한이 없습니다.";

    public UnAuthorizationException() {
        super(DEFAULT_MESSAGE);
    }
}
