package com.codesoom.assignment.errors.user;

import com.codesoom.assignment.common.message.ErrorMessage;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException() {
        super(ErrorMessage.USER_NOT_EXIST.getErrorMsg());
    }
}
