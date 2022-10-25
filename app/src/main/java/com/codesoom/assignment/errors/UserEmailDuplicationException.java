package com.codesoom.assignment.errors;

/**
 * 이미 사용중인 이메일일 경우 던집니다.
 */
public class UserEmailDuplicationException extends RuntimeException {
    public UserEmailDuplicationException(String email) {
        super("User email is alreay existed: " + email);
    }
}
