package com.codesoom.assignment.errors;

public class UserNotMatchException extends RuntimeException {
    public UserNotMatchException(Long id) {
        super("자기 자신만 수정할 수 있습니다 " + id);
    }
}
