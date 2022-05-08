package com.codesoom.assignment.authorization;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 사용자 권한을 인증한다.
 * 요청 시 받은 인증 토큰을 검증해
 * 실제 사용자인지 확인한다.
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@PreAuthorize("isAuthenticated()")
public @interface AuthenticateUser {
}
