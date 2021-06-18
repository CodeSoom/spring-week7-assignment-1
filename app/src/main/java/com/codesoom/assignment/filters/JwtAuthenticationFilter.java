package com.codesoom.assignment.filters;

import com.codesoom.assignment.application.AuthenticationService;
import com.codesoom.assignment.security.UserAuthentication;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

/**
 * 요청에 포함된 JWT를 관리합니다.
 */
public class JwtAuthenticationFilter extends BasicAuthenticationFilter {
    private final AuthenticationService authenticationService;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager,
                                   AuthenticationService authenticationService) {
        super(authenticationManager);
        this.authenticationService = authenticationService;
    }

    /**
     * 요청에 포함된 `Authorization` 헤더를 검증하고 다음 메소드로 요청을 전달합니다.
     *
     * @param request 받은 요청
     * @param response 보낼 응답
     * @param chain 필터 컬렉션
     * @throws IOException
     * @throws ServletException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws IOException, ServletException {

        Optional.ofNullable(request.getHeader("Authorization"))
                .map(authorization -> authorization.substring("Bearer ".length()))
                .map(authenticationService::parseToken)
                .map(UserAuthentication::new)
                .ifPresent(authentication -> {
                    SecurityContext context = SecurityContextHolder.getContext();
                    context.setAuthentication(authentication);
                });

        chain.doFilter(request, response);
    }
}
