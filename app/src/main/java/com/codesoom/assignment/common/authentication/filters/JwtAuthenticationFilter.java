package com.codesoom.assignment.common.authentication.filters;

import com.codesoom.assignment.common.authentication.security.UserAuthentication;
import com.codesoom.assignment.session.application.AuthenticationService;
import org.springframework.http.HttpHeaders;
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

public class JwtAuthenticationFilter extends BasicAuthenticationFilter {
    private final AuthenticationService authenticationService;

    public JwtAuthenticationFilter(final AuthenticationManager authenticationManager,
                                   final AuthenticationService authenticationService) {
        super(authenticationManager);
        this.authenticationService = authenticationService;
    }

    @Override
    protected void doFilterInternal(final HttpServletRequest request,
                                    final HttpServletResponse response,
                                    final FilterChain chain) throws IOException, ServletException {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorization != null) {
            Long userId = getUserIdByAccessToken(authorization);
            setAuthentication(userId);
        }

        chain.doFilter(request, response);
    }

    private static void setAuthentication(Long userId) {
        Authentication authentication = new UserAuthentication(userId);

        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(authentication);
    }

    private Long getUserIdByAccessToken(String authorization) {
        String accessToken = authorization.substring("Bearer ".length());
        return authenticationService.parseToken(accessToken);
    }
}
