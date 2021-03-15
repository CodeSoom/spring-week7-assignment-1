package com.codesoom.assignment.config;

import com.codesoom.assignment.application.AuthenticationService;
import com.codesoom.assignment.filters.AuthenticationErrorFilter;
import com.codesoom.assignment.filters.JwtAuthenticationFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

import javax.servlet.Filter;

/**
 * Spring Security 설정.
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true) // 특정 주소 접근시 권한 및 인증을 pre(미리) 체킹
public class SecurityJavaConfig extends WebSecurityConfigurerAdapter {

    private final AuthenticationService authenticationService;

    public SecurityJavaConfig(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        // authenticationFilter : Jwt가 유효한 토큰인지 인증하기 위한 필터
        // authenticationManager : Spring Security의 필터들이 인증을 수행하는 방법에 대한 명세를 정의해 놓은 인터페이스
        Filter authenticationFilter = new JwtAuthenticationFilter(
                authenticationManager(), authenticationService);

        // 유저 인증 시 발생하는 에러처리 필터
        Filter authenticationErrorFilter = new AuthenticationErrorFilter();

        http
                .csrf().disable() // CSRF 보호 기능 비활성화
                .addFilter(authenticationFilter) // Jwt가 유효한 토큰인지 확인
                .addFilterBefore(authenticationErrorFilter, // addFilterBefore : authenticationErrorFilter를 JwtAuthenticationFilter보다 먼저 동작하게 등록함
                        JwtAuthenticationFilter.class)
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 스프링 시큐리티 인증 처리 관점에서 세션을 생성하지 않음과 동시에, 세션을 이용한 방식으로 인증을 처리하지 않겠다는 의미
                .and()
                .exceptionHandling()
                .authenticationEntryPoint( // 인증되지 않은 요청에 대해 UNAUTHORIZED로 처리
                        new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));

      http.headers().frameOptions().disable(); // h2-console을 사용할 수 없는 에러를 처리하기 위한 코드
    }
}
