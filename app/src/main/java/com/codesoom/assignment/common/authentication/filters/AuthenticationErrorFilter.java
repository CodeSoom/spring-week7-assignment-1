package com.codesoom.assignment.common.authentication.filters;

import com.codesoom.assignment.session.application.exception.InvalidTokenException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 다음 필터(JwtAuthenticationFilter)에서 진행되는 인증(Authentication) 작업 중
 * 토큰 검증 예외 발생 시, 예외를 핸들링하여 응답을 반환합니다.
 */
public class AuthenticationErrorFilter extends HttpFilter {
    @Override
    protected void doFilter(HttpServletRequest request,
                            HttpServletResponse response,
                            FilterChain chain) throws IOException, ServletException {
        try {
            chain.doFilter(request, response);
        } catch (InvalidTokenException exception) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}
