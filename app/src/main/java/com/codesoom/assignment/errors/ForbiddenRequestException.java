package com.codesoom.assignment.errors;

public class ForbiddenRequestException extends RuntimeException {
    public ForbiddenRequestException() {
        super("금지된 요청입니다.");
    }
}
