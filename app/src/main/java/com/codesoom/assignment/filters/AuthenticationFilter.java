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
        if (filterWithPathAndMethod(request)) {
            chain.doFilter(request, response);
            return;
        }

        doAuthentication(request, response);

        chain.doFilter(request, response);
    }

    private boolean filterWithPathAndMethod(HttpServletRequest request) {
        String path = request.getRequestURI();
        if (!path.startsWith("/products")) {
            return true;
        }

        String method = request.getMethod();
        if (method.equals("GET")) {
            return true;
        }

        if (method.equals("OPTIONS")) {
            return true;
        }

        return false;
    }

    private boolean doAuthentication(HttpServletRequest request,
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
