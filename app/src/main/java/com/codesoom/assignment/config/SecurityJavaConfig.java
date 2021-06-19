package com.codesoom.assignment.config;

import com.codesoom.assignment.application.AuthenticationService;
import com.codesoom.assignment.filters.AuthenticationErrorFilter;
import com.codesoom.assignment.filters.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

import javax.servlet.Filter;

/**
 * SecurityJavaConfig class
 *
 * 각종 spring security를 설정하는 클래스입니다.
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityJavaConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private AuthenticationService authenticationService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        /**
         * JwtAuthenticationFilter을 사용하기 위해서 authenticationManager()을 매개변수로 받음
         * (authenticationManager : Spring Security의 필터들이 인증을 수행하는 방법에 대한 명세를 정의해 놓은 인터페이스)
         */
        Filter authenticationFilter = new JwtAuthenticationFilter(authenticationManager(), authenticationService);
        Filter authenticationErrorFilter = new AuthenticationErrorFilter();


        http
                .csrf().disable()  // CSRF(Cross-Site Request Forgery) 비활성화
                .addFilter(authenticationFilter)
                .addFilterBefore(authenticationErrorFilter,
                                JwtAuthenticationFilter.class)
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
                .exceptionHandling()
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
        http.headers().frameOptions().disable();
    }
}
