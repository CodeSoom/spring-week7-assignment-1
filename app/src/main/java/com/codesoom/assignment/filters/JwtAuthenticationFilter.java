package com.codesoom.assignment.filters;

import com.codesoom.assignment.application.AuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthenticationFilter extends BasicAuthenticationFilter {
    private final AuthenticationService authenticationService;

    public JwtAuthenticationFilter(
            AuthenticationManager authenticationManager,
            AuthenticationService authenticationService) {
        super(authenticationManager);
        this.authenticationService = authenticationService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws IOException, ServletException {
        // TODO : authentication
        String authorization = request.getHeader("Authorization");

        if (authorization == null) {
            response.sendError(HttpStatus.UNAUTHORIZED.value());
            return;
        }

        String accessToken = authorization.substring("Bearer ".length());
        Long userId = authenticationService.parseToken(accessToken);

        request.setAttribute("userId", userId);
        chain.doFilter(request, response);
    }
}
