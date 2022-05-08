package com.codesoom.assignment.authorization;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 사용자 권한을 인가한다.
 * 작업을 수행하기 위해 Path variable 로 받은 userId 와
 * 요청 시 받은 토큰을 디코드한 userId 가 일치하는지 확인한다.
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@PreAuthorize("isAuthenticated() and principal.equals(#userId)")
public @interface AuthorizeUser {
}
