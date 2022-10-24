package com.codesoom.assignment.errors;

import lombok.Generated;

/**
 * 유효하지 않는 값이 있을경우 던집니다.
 */
@Generated
public class InvalidParamException extends RuntimeException {
    public InvalidParamException(String message) {
        super(message);
    }
}
