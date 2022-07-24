package com.codesoom.assignment.filter;

import com.codesoom.assignment.application.AuthenticationService;
import com.codesoom.assignment.security.UserAuthentication;
import io.jsonwebtoken.Claims;
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
import java.util.logging.Filter;

public class JwtAuthenticationFilter extends BasicAuthenticationFilter {

    private final String AUTHENTICATION_HEADER = "Authentication";
    private final String BEARER_PREFIX = "Bearer ";
    private AuthenticationService authenticationService;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, AuthenticationService authenticationService) {
        super(authenticationManager);
        this.authenticationService = authenticationService;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String headerAuthenticationField = request.getHeader(AUTHENTICATION_HEADER);

        if (headerAuthenticationField != null) {
            String accessToken = headerAuthenticationField.substring(BEARER_PREFIX.length());
            Long id = authenticationService.parseToken(accessToken);

            Authentication authentication = new UserAuthentication(id);

            SecurityContext context = SecurityContextHolder.getContext();
            context.setAuthentication(authentication);
        }

        chain.doFilter(request, response);
    }
}
