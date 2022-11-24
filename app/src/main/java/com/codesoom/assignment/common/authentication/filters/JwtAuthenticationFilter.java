package com.codesoom.assignment.common.authentication.filters;

import com.codesoom.assignment.session.application.AuthenticationService;
import com.codesoom.assignment.session.application.exception.InvalidTokenException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
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
        if (isNotRequiredAuthorization(request)) {
            chain.doFilter(request, response);
            return;
        }

        validateAuthToken(request);

        chain.doFilter(request, response);
    }

    private void validateAuthToken(final HttpServletRequest request) {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorization == null) {
            throw new InvalidTokenException("");
        }

        String accessToken = authorization.substring("Bearer ".length());
        authenticationService.parseToken(accessToken);
    }

    private boolean isNotRequiredAuthorization(final HttpServletRequest request) {
        if (!request.getRequestURI().startsWith("/products")) {
            return true;
        }

        if (HttpMethod.GET.toString().equals(request.getMethod())
                || HttpMethod.OPTIONS.toString().equals(request.getMethod())) {
            return true;
        }

        return false;
    }
}
