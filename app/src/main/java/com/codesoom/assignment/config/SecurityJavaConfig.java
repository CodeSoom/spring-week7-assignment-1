package com.codesoom.assignment.config;

import com.codesoom.assignment.filters.AuthenticationErrorFilter;
import com.codesoom.assignment.filters.JwtAuthenticationFilter;
import com.codesoom.assignment.utils.JwtUtil;
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

import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityJavaConfig extends WebSecurityConfigurerAdapter {
    private final JwtUtil jwtUtil;
    public SecurityJavaConfig(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {

        Filter authenticationFilter = new JwtAuthenticationFilter(authenticationManager(), jwtUtil);
        Filter authenticationErrorFilter = new AuthenticationErrorFilter();

        http
                .csrf().disable()
                .addFilter(authenticationFilter)
                .addFilterBefore(authenticationErrorFilter, JwtAuthenticationFilter.class)
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and();

        configureAuthorizations(http);

        http
                .authorizeRequests()
                .anyRequest().permitAll()
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
    }

    private void configureAuthorizations(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .requestMatchers(this::matchesPostProductRequest).authenticated()
                .and()
                .authorizeRequests()
                .requestMatchers(this::matchesPatchProductRequest).authenticated()
                .and()
                .authorizeRequests()
                .requestMatchers(this::matchesDeleteProductRequest).authenticated()
                .and()
                .authorizeRequests()
                .requestMatchers(this::matchesPostUserRequest).authenticated()
                .and()
                .authorizeRequests()
                .requestMatchers(this::matchesDeleteUserRequest).authenticated();
    }

    /**
     * 요청이 상품 생성 요청인 경우 true를 아닌경우 false를 반환합니다.
     * @param req
     * @return
     */
    private boolean matchesPostProductRequest(HttpServletRequest req) {
        return req.getMethod().equals("POST") &&
                (req.getRequestURI().matches("^/products$") ||
                        req.getRequestURI().matches("^/products/[0-9]+$"));
    }

    /**
     * 요청이 상품 수정 요청인 경우 true를 아닌경우 false를 반환합니다.
     * @param req
     * @return
     */
    private boolean matchesPatchProductRequest(HttpServletRequest req) {
        return req.getMethod().equals("PATCH") &&
                        req.getRequestURI().matches("^/products/[0-9]+$");
    }

    /**
     * 요청이 상품 삭제 요청인 경우 true를 아닌경우 false를 반환합니다.
     * @param req
     * @return
     */
    private boolean matchesDeleteProductRequest(HttpServletRequest req) {
        return req.getMethod().equals("DELETE") &&
                req.getRequestURI().matches("^/products/[0-9]+$");
    }

    /**
     * 요청이 유저 생성 요청인 경우 true를 아닌경우 false를 반환합니다.
     * @param req
     * @return
     */
    private boolean matchesPostUserRequest(HttpServletRequest req) {
        return req.getMethod().equals("POST") && req.getRequestURI().matches("^/users/[0-9]+$");
    }

    /**
     * 요청이 유저 삭제 요청인 경우 true를 아닌경우 false를 반환합니다.
     * @param req
     * @return
     */
    private boolean matchesDeleteUserRequest(HttpServletRequest req) {
        return req.getMethod().equals("DELETE") && req.getRequestURI().matches("^/users/[0-9]+$");
    }


}
