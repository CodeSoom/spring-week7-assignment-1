package com.codesoom.assignment.errors;

/**
 * 사용자를 찾을 수 없을 경우 던집니다.
 */
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long id) {
        super("User not found: " + id);
    }
}
