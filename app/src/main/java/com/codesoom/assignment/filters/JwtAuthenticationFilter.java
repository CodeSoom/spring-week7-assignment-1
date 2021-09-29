package com.codesoom.assignment.filters;

import com.codesoom.assignment.application.AuthenticationService;
import com.codesoom.assignment.errors.NotValidTokenException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthenticationFilter extends BasicAuthenticationFilter {

    private final AuthenticationService authenticationService;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, AuthenticationService authenticationService) {
        super(authenticationManager);
        this.authenticationService = authenticationService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (filterWithPathAndMethod(request)) {
            chain.doFilter(request,response);
            return ;
        }

        String authorization = request.getHeader("Authorization");

        if (authorization == null) {
            throw new NotValidTokenException();
        }

        String token = authorization.substring("Bearer ".length());

        authenticationService.parseToken(token);

        chain.doFilter(request,response);

    }

    private boolean filterWithPathAndMethod(HttpServletRequest request) {

        String method = request.getMethod();
        String requestURI = request.getRequestURI();

        if (!requestURI.equals("/products") || "GET".equals(method)) {
            return true;
        }

        return false;

    }

}
