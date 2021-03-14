package com.codesoom.assignment.errors;

/**
 * 비허가 접근 예외
 * e.g. 유저가 다른 유저의 정보를 수정하려고 할 때, 해당 예외 객체가 던져짐.
 */
public class UnauthorizedAccessException extends RuntimeException {
    public UnauthorizedAccessException() {
        super("Unauthorized access");
    }
}
