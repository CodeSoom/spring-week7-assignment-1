package com.codesoom.assignment.errors.user;

import com.codesoom.assignment.common.message.ErrorMessage;

public class UserEmailDuplicationException extends RuntimeException {
    public UserEmailDuplicationException(String email) {
        super(ErrorMessage.EMAIL_IS_DUPLICATE.getErrorMsg());
    }
}
