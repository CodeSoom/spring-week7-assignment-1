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
 * HTTP 요청이 실패하는 경우를 처리합니다.
 */
@ResponseBody
@ControllerAdvice
public class ControllerErrorAdvice {
    /**
     * 상품을 찾지 못한 경우에 대한 에러 응답을 리턴합니다.
     * @return 에러 응답
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ProductNotFoundException.class)
    public ErrorResponse handleProductNotFound() {
        return new ErrorResponse("Product not found");
    }

    /**
     * 회원을 찾지 못한 경우에 대한 에러 응답을 리턴합니다.
     * @return 에러 응답
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UserNotFoundException.class)
    public ErrorResponse handleUserNotFound() {
        return new ErrorResponse("User not found");
    }

    /**
     * 금지된 요청을 하는 경우에 대한 에러 응답을 리턴합니다.
     * @param e 금지된 요청을 한 경우에 던지는 예외
     * @return 에러 응답
     */
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(ForbiddenRequestException.class)
    public ErrorResponse handleForbiddenRequest(ForbiddenRequestException e) {
        return new ErrorResponse(e.getMessage());
    }

    /**
     * 이메일이 중복된 경우에 대한 에러 응답을 리턴합니다.
     * @return 에러 응답
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UserEmailDuplicationException.class)
    public ErrorResponse handleUserEmailIsAlreadyExisted() {
        return new ErrorResponse("User's email address is already existed");
    }

    /**
     * 로그인이 실패하는 경우에 대한 에러 응답을 리턴합니다.
     * @return 에러 응답
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(LoginFailException.class)
    public ErrorResponse handleLoginFailException() {
        return new ErrorResponse("Log-in failed");
    }

    /**
     * 잘못된 토큰으로 요청한 경우에 대한 에러 응답을 리턴합니다.
     * @return 에러 응답
     */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(InvalidTokenException.class)
    public ErrorResponse handleInvalidAccessTokenException() {
        return new ErrorResponse("Invalid access token");
    }

    /**
     * 유호셩 검사가 실패한 경우에 대한 에러 응답을 리턴합니다.
     * @param exception 유효성 검사가 실패한 경우 던지는 예외
     * @return 에러 메시지
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
     * 유효성 검사가 실패한 경우에 대한 에러 메시지를 리턴합니다.
     * @param exception 유효성 검사가 실패한 경우 던지는 예외
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
