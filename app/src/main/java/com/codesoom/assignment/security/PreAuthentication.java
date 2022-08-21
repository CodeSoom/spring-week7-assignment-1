package com.codesoom.assignment.security;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 인증이 되었을 때만 특정 method 가 호출되도록 지정하는 annotation 입니다.
 * Spring security 의 `@PreAuthorize` 통해서 인증을 확인합니다.
 */
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("isAuthenticated()")
public @interface PreAuthentication {
}
