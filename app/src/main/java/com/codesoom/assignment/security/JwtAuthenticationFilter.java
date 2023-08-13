package com.codesoom.assignment.security;

import com.codesoom.assignment.application.AuthenticationService;
import com.codesoom.assignment.errors.InvalidTokenException;
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

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, AuthenticationService authenticationService) {
        super(authenticationManager);
        this.authenticationService = authenticationService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        String authorization = request.getHeader("Authorization");

        try {
            if(authorization == null){
                    throw new InvalidTokenException("");
            }

            String accessToken = authorization.substring("Bearer ".length());
            authenticationService.parseToken(accessToken);

        }catch (InvalidTokenException e){
            response.sendError(HttpStatus.UNAUTHORIZED.value());
            return;
        }

        chain.doFilter(request,response);
    }
}
