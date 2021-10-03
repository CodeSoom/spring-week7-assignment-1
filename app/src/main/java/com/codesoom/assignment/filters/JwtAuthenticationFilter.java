package com.codesoom.assignment.filters;

import com.codesoom.assignment.application.AuthenticationService;
import com.codesoom.assignment.errors.InvalidTokenException;
import com.codesoom.assignment.securities.Roles;
import com.codesoom.assignment.securities.UserAuthentication;
import org.springframework.http.HttpStatus;
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

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, AuthenticationService authenticationService) {
        super(authenticationManager);
        this.authenticationService = authenticationService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {
        try {
            String accessToken = parseAuthorizationHeaderFrom(request);

            if(!accessToken.isBlank()) {
                Authentication authResult = this.getAuthenticationManager().authenticate(
                        new UserAuthentication(Roles.ANONYMOUS, accessToken)
                );

                onSuccessfulAuthentication(request, response, authResult);
            }

            chain.doFilter(request, response);
        } catch (InvalidTokenException e){
            response.sendError(HttpStatus.UNAUTHORIZED.value());
        }
    }

    @Override
    protected void onSuccessfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authResult
    ) {
        authResult.setAuthenticated(true);

        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(authResult);
    }

    private String parseAuthorizationHeaderFrom(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");

        if (authorization == null) {
            return "";
        }

        return authorization.substring("Bearer ".length());
    }
}
