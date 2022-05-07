package com.codesoom.assignment.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

@Configuration
// Method Security 참고: https://docs.spring.io/spring-security/reference/servlet/authorization/method-security.html
// @PreAuthorize, @PostAuthorize, @PreFilter, @PostFilter 애노테이션의 사용을 가능하게 한다.
// 스프링 시큐리티 최신 버전(5.6 이상)에서는 @EnableMethodSecurity 를 사용한다.
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf()
                // 현재는 단순 API 형태로 사용할 것이기 때문에 CSRF 기능을 비활성화한다.
                .disable()
                .sessionManagement()
                    // 세션, 쿠키방식의 인증 매커니즘을 쓰지 않겠다. 매번 인증을 받겠다.
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(
                        // 인증이 되어있지 않아 예외가 발생한 경우 내려줄 상태 코드 입력
                        new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)
                );
    }
}
