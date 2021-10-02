package com.codesoom.assignment.filters;

import com.codesoom.assignment.application.AuthenticationService;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

public class AuthenticationFilter extends BasicAuthenticationFilter {

    private final AuthenticationService authenticationService;

    public AuthenticationFilter(AuthenticationManager authenticationManager,
        AuthenticationService authenticationService) {
        super(authenticationManager);
        this.authenticationService = authenticationService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain chain) throws IOException, ServletException {
        if (!isRequireAuthenticate(request)) {
            chain.doFilter(request, response);
            return;
        }

        authenticate(request, response);

        chain.doFilter(request, response);
    }

    private boolean isRequireAuthenticate(HttpServletRequest request) {
        String path = request.getRequestURI();
        if (!isRequireAuthenticatePath(path)) {
            return false;
        }

        String method = request.getMethod();
        if (!isRequireAuthenticateMethod(method)) {
            return false;
        }

        return true;
    }

    private boolean isRequireAuthenticatePath(String path) {
        return path.startsWith("/products");
    }

    private boolean isRequireAuthenticateMethod(String method) {
        return !(method.equals("GET") || method.equals("OPTIONS"));
    }

    private boolean authenticate(HttpServletRequest request,
        HttpServletResponse response)
        throws IOException {
        String authorization = request.getHeader("Authorization");

        if (authorization == null) {
            response.sendError(HttpStatus.UNAUTHORIZED.value());
            return false;
        }

        String accessToken = getAccessToken(authorization);
        Long userId = authenticationService.parseToken(accessToken);

        request.setAttribute("userId", userId);

        return true;
    }

    private String getAccessToken(String authorization) {
        return authorization.substring("Bearer ".length());
    }
}
