package com.codesoom.assignment.filters;

import com.codesoom.assignment.application.AuthenticationService;
import com.codesoom.assignment.security.UserAuthentication;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JWT 인증 전처리기.
 */
public class JwtAuthenticationFilter extends GenericFilterBean {
    private final AuthenticationService authenticationService;

    private static final String AUTH_HEADER = "Authorization";
    private static final String BEARER_TOKEN_PREFIX = "Bearer ";

    public JwtAuthenticationFilter(
            AuthenticationService authenticationService) {
        //super(authenticationManager);
        this.authenticationService = authenticationService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        doAuthentication((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    /**
     * 주어진 요청으로 유저 인증을 수행한다.
     *
     * @param request  요청 정보
     * @param response 응답 정보
     * @param chain    필터 페인
     * @throws IOException
     * @throws ServletException
     */
    private void doAuthentication(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain chain)
            throws IOException, ServletException {
        String authorization = request.getHeader(AUTH_HEADER);

        if (authorization != null) {
            String accessToken = authorization.substring(BEARER_TOKEN_PREFIX.length());
            Long userId = authenticationService.parseToken(accessToken);

            Authentication authentication = new UserAuthentication(userId);

            SecurityContext context = SecurityContextHolder.getContext();
            context.setAuthentication(authentication);
        }
        chain.doFilter(request, response);
    }
}
