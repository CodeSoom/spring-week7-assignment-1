package com.codesoom.assignment.config;

import com.codesoom.assignment.application.AuthenticationService;
import com.codesoom.assignment.security.JwtAccessDeniedHandler;
import com.codesoom.assignment.security.JwtAuthenticationFilter;
import com.codesoom.assignment.utils.JwtUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final AuthenticationService authenticationService;
    private final JwtUtil jwtUtil;

    public SecurityConfig(AuthenticationService authenticationService,
                          JwtUtil jwtUtil) {
        this.authenticationService = authenticationService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // CSRF 지원 비활성화
                .csrf().disable()

                // HttpSession 생성 및 사용 안함
                .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()

                // 인증을 처리할 필터 지정
                .addFilter(new JwtAuthenticationFilter(authenticationManager(), authenticationService, jwtUtil))

                // 예외 처리
                .exceptionHandling()
                    .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                    .accessDeniedHandler(new JwtAccessDeniedHandler());
    }
}
