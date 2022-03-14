package com.codesoom.assignment.filters;

import com.codesoom.assignment.application.AuthenticationService;
import com.codesoom.assignment.errors.InvalidTokenException;
import com.codesoom.assignment.security.Roles;
import com.codesoom.assignment.security.UserAuthentication;
import org.springframework.http.HttpMethod;
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
            FilterChain chain)
            throws IOException, ServletException {

        if (filterWithPath(request) && filterWithMethod(request)) {
            chain.doFilter(request, response);
            return;
        }

        try {
            String accessToken = parseAuthorizationHeaderFrom(request);

            if (!accessToken.isBlank()) {
                Authentication authentication = this.getAuthenticationManager()
                        .authenticate(
                                new UserAuthentication(Roles.ANONYMOUS, accessToken)
                        );
                SecurityContext context = SecurityContextHolder.getContext();
                authentication.setAuthenticated(true);
                context.setAuthentication(authentication);
            }
            chain.doFilter(request, response);
        } catch (InvalidTokenException e) {
            response.sendError(HttpStatus.UNAUTHORIZED.value());
        }
    }

    private boolean filterWithMethod(HttpServletRequest request) {
        String method = request.getMethod();
        return (method.equals(HttpMethod.GET));
    }

    private boolean filterWithPath(HttpServletRequest request) {
        String path = request.getRequestURI();
        return !(path.startsWith("/products"));
    }

    private String parseAuthorizationHeaderFrom(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");

        if (authorization == null) {
            return "";
        }
        return authorization.substring("Bearer ".length());
    }
}
