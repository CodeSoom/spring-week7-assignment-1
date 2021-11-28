package com.codesoom.assignment.dto;

/**
 * 예외 메세지 정보
 */
public class ErrorResponse {
    private String message;

    public ErrorResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
