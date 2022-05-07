package com.codesoom.assignment.errors;

import com.codesoom.assignment.common.message.ErrorMessage;

public class CustomInternalServerException extends BaseException {
    public CustomInternalServerException(String message, ErrorMessage errorCode) {
        super(message, errorCode);
    }

    public CustomInternalServerException(ErrorMessage errorCode) {
        super(errorCode.getErrorMsg(), errorCode);
    }
}
