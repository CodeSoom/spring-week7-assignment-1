package com.codesoom.assignment.controllers;

import com.codesoom.assignment.dto.ErrorResponse;
import com.codesoom.assignment.errors.InvalidTokenException;
import com.codesoom.assignment.errors.LoginFailException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 세션 관련 예외처리를 담당합니다.
 */
@ResponseBody
@ControllerAdvice
public class SessionErrorAdvice {

    /**
     * 로그인에 실패 했을 때 에러 응답을 리턴합니다.
     * @return 에러 응답
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(LoginFailException.class)
    public ErrorResponse handleLoginFailException() {
        return new ErrorResponse("Log-in failed");
    }

    /**
     * 엑세스 토큰이 유효하지 않을 때 에러 응답을 리턴합니다.
     * @return 에러 응답
     */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(InvalidTokenException.class)
    public ErrorResponse handleInvalidAccessTokenException() {
        return new ErrorResponse("Invalid access token");
    }

    /**
     * 엑세스 토큰을 바인딩에 실패 할 때 에러 응답을 리턴합니다.
     * @return 에러 응답
     */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(ServletRequestBindingException.class)
    public ErrorResponse handleServletRequestBindingExceptionAboutAccessToken() {
        return new ErrorResponse("Not Existed Access Token");
    }

}
