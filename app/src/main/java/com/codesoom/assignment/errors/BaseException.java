package com.codesoom.assignment.errors;


import com.codesoom.assignment.common.message.ErrorMessage;

public class BaseException extends RuntimeException {
    private ErrorMessage loggingDisplayErrorMessage;

    public BaseException() {
    }

    public BaseException(ErrorMessage errorCode) {
        super(errorCode.getErrorMsg());
        this.loggingDisplayErrorMessage = errorCode;
    }

    public BaseException(String loggingDisplayMessage, ErrorMessage loggingDisplayErrorMessage) {
        super(loggingDisplayMessage);
        this.loggingDisplayErrorMessage = loggingDisplayErrorMessage;
    }

    public BaseException(String loggingDisplayMessage, ErrorMessage loggingDisplayErrorMessage, Throwable cause) {
        super(loggingDisplayMessage, cause);
        this.loggingDisplayErrorMessage = loggingDisplayErrorMessage;
    }
}
