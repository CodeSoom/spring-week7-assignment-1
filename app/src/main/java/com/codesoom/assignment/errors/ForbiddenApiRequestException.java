package com.codesoom.assignment.errors;

public class ForbiddenApiRequestException extends RuntimeException {
    public ForbiddenApiRequestException() {
        super("타인의 정보를 수정할 수 없습니다.");
    }
}
