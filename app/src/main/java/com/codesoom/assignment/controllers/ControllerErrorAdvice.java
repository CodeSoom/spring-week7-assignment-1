package com.codesoom.assignment.controllers;

import com.codesoom.assignment.dto.ErrorResponse;
import com.codesoom.assignment.errors.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;

/**
 * HTTP 요청의 실패에 대한 처리
 */
@ResponseBody
@ControllerAdvice
public class ControllerErrorAdvice {
    /**
     * 상품을 찾지 못하는 경우에는 에러 응답을 반환한다
     *
     * @return 에러 응답
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ProductNotFoundException.class)
    public ErrorResponse handleProductNotFound() {
        return new ErrorResponse("Product not found");
    }

    /**
     * 회원을 찾지 못하는 경우에는 에러 응답을 반환한다
     *
     * @return 에러 응답
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UserNotFoundException.class)
    public ErrorResponse handleUserNotFound() {
        return new ErrorResponse("User not found");
    }

    /**
     * 이메일이 중복되는 경우에는 에러 응답을 반환한다
     *
     * @return 에러 응답
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UserEmailDuplicationException.class)
    public ErrorResponse handleUserEmailIsAlreadyExisted() {
        return new ErrorResponse("User's email address is already existed");
    }

    /**
     * 로그인이 실패하는 경우에는 에러 응답을 반환한다
     *
     * @return 에러 응답
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(LoginFailException.class)
    public ErrorResponse handleLoginFailException() {
        return new ErrorResponse("Log-in failed");
    }

    /**
     * 유효성 검사가 실패하는 경우에는 에러 응답을 반환한다
     *
     * @param exception 유효성 검사가 실패하는 경우의 예외 처리
     * @return 에러 응답
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

    /**
     * 유효성 검사가 실패하는 경우에는 에러 메시지를 반환한다
     *
     * @param exception 유효성 검사가 실패하는 경우의 예외 처리
     * @return 에러 메시지
     */
    private String getViolatedMessage(ConstraintViolationException exception) {
        String messageTemplate = null;
        Set<ConstraintViolation<?>> violations = exception.getConstraintViolations();
        for (ConstraintViolation<?> violation : violations) {
            messageTemplate = violation.getMessageTemplate();
        }
        return messageTemplate;
    }
}
