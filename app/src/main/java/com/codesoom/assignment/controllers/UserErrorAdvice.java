package com.codesoom.assignment.controllers;

import com.codesoom.assignment.dto.ErrorResponse;
import com.codesoom.assignment.errors.UserEmailDuplicationException;
import com.codesoom.assignment.errors.UserNotFoundException;
import com.codesoom.assignment.errors.UserNotMatchException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;

/**
 * 사용자 관련 예외처리를 담당합니다.
 */
@ResponseBody
@ControllerAdvice
public class UserErrorAdvice {

    /**
     * 사용자를 찾지 못 했을때 에러 응답을 리턴합니다.
     * @return 에러 응답
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UserNotFoundException.class)
    public ErrorResponse handleUserNotFound() {
        return new ErrorResponse("User not found");
    }

    /**
     * 사용자 이메일이 중복 될 때 에러 응답을 리턴합니다.
     * @return 에러 응답
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UserEmailDuplicationException.class)
    public ErrorResponse handleUserEmailIsAlreadyExisted() {
        return new ErrorResponse("User's email address is already existed");
    }

    /**
     * DTO로 바인딩 실패 할 때 에러 응답을 리턴합니다.
     * @param exception
     * @return
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public ErrorResponse handleConstraintValidateError(
            ConstraintViolationException exception
    ) {
        String messageTemplate = getViolatedMessage(exception);
        return new ErrorResponse(messageTemplate);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UserNotMatchException.class)
    public ErrorResponse handleUserNotMatch() {
        return new ErrorResponse("자기 자신만 수정할 수 있습니다");
    }

    private String getViolatedMessage(ConstraintViolationException exception) {
        String messageTemplate = null;
        Set<ConstraintViolation<?>> violations = exception.getConstraintViolations();
        for (ConstraintViolation<?> violation : violations) {
            messageTemplate = violation.getMessageTemplate();
        }
        return messageTemplate;
    }
}
