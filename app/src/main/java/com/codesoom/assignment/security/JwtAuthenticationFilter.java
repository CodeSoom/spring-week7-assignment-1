package com.codesoom.assignment.security;

import com.codesoom.assignment.application.AuthenticationService;
import com.codesoom.assignment.utils.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.util.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Authorization 헤더를 통해 회원 인증을 수행하는 필터.
 */
public class JwtAuthenticationFilter extends BasicAuthenticationFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER = "Bearer ";

    private final AuthenticationService authenticationService;
    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager,
                                   AuthenticationService authenticationService,
                                   JwtUtil jwtUtil) {
        super(authenticationManager);

        this.authenticationService = authenticationService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws IOException, ServletException {
        String accessToken = resolveToken(request);

        if (StringUtils.hasText(accessToken) && jwtUtil.validateToken(accessToken)) {
            Long userId = authenticationService.parseToken(accessToken);

            SecurityContext context = SecurityContextHolder.getContext();
            UserAuthentication userAuthentication = new UserAuthentication(userId);
            context.setAuthentication(userAuthentication);
        }

        chain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String authorization = request.getHeader(AUTHORIZATION_HEADER);

        if (StringUtils.hasText(authorization) && authorization.startsWith(BEARER)) {
            return authorization.substring(BEARER.length());
        }

        return null;
    }
}
