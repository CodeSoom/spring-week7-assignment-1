package com.codesoom.assignment.errors.user;

public class UserCommonException extends RuntimeException {
    public UserCommonException(String errorMessage) {
        super(errorMessage);
    }
}
