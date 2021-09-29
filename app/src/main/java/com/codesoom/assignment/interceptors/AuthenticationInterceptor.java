package com.codesoom.assignment.interceptors;

import com.codesoom.assignment.application.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

    @Autowired
    private AuthenticationService authenticationService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String authorization = request.getHeader("Authorization");
        String method = request.getMethod();
        String requestURI = request.getRequestURI();

        if (!requestURI.equals("/products") || "GET".equals(method)) {
            return true;
        }

        if (authorization == null) {
            response.sendError(HttpStatus.UNAUTHORIZED.value());
            return false;
        }

        String token = authorization.substring("Bearer ".length());
        authenticationService.parseToken(token);
        return true;

    }

}
