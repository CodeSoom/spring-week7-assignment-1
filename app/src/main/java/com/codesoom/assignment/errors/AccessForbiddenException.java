package com.codesoom.assignment.errors;

/**
 * Forbidden 은 Unauthorized 와는 다르다.
 * 인증은 되어 있는 상태이지만, 해당 동작을 수행할 권한이 없을 경우 발생한다.
 * HTTP 응답에서 403 Forbidden 상태코드를 반환한다.
 */
public class AccessForbiddenException extends RuntimeException{
    public AccessForbiddenException(String message) {
        super(message);
    }
}
