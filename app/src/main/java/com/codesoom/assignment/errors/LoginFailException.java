package com.codesoom.assignment.errors;

/**
 * 로그인 실패에 대한 예외 클래스
 */
public class LoginFailException extends RuntimeException{
    public LoginFailException(String email) {
        super("Login fail - email: " + email);
    }
}
