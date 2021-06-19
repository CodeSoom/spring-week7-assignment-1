package com.codesoom.assignment.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * SecurityJavaConfig class
 *
 * 각종 spring security를 설정하는 클래스입니다.
 */
@Configuration
public class SecurityJavaConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                .csrf().disable();  // CSRF(Cross-Site Request Forgery) 비활성화

    }
}