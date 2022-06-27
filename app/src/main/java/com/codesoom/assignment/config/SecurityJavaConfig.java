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

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityJavaConfig extends WebSecurityConfigurerAdapter {

    private AuthenticationService authenticationService;

    public SecurityJavaConfig(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        Filter authenticationFilter = new JwtAuthenticationFilter(
                authenticationManager(), authenticationService);
        Filter authenticationErrorFilter = new AuthenticationErrorFilter();

        http
                .headers().frameOptions().disable() // headers 옵션 중 frameOptions만 비활성화
                .and()
                .csrf().disable()  // csrf 비활성화
                .addFilter(authenticationFilter) // 커스텀 필터를 지정
                .addFilterBefore(authenticationErrorFilter,
                        // addFilterBefore(A, B.class): B.class 이전에 A 필터를 호출
                        JwtAuthenticationFilter.class) //  JwtAuthenticationFilter
                // Form Login 시 걸리는 Filter이다. UsernamePasswordAuthenticationFilter를 상속한 JwtAuthenticationFilter을 등록
                // 이 필터는 HttpServletRequest에서 사용자가 Form으로 입력한 로그인 정보를 인터셉트해서 AuthenticationManager에게
                // Authentication 객체를 넘겨준다.
                .sessionManagement()
                // 시간 초과 감지, 동시 세션 제어, Session Fixation 공격 보호
                // SessionManagementFilter, SessionAuthentication 전략
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) //
                .and()
                .exceptionHandling() // 예외 핸들러를 등록
                .authenticationEntryPoint(
                        // 현재 인증된 사용자가 없는데(SecurityContext에 인증사용자가 담기지 않음)
                        // 인증되지 않은 사용자가 보호된 리소스에 접근하였을 때, 수행되는 핸들러(EntryPoint)를 등록
                        new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
    }
}
