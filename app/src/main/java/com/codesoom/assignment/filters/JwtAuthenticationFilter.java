package com.codesoom.assignment.filters;

import com.codesoom.assignment.application.AuthenticationService;
import com.codesoom.assignment.authentication.UserAuthentication;
import org.springframework.http.HttpMethod;
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
import java.util.HashSet;
import java.util.Set;

import static org.springframework.util.StringUtils.hasText;

public class JwtAuthenticationFilter extends BasicAuthenticationFilter {
    private final AuthenticationService authenticationService;
    private final Set<HttpMethod> targetMethods = new HashSet<>();

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, AuthenticationService authenticationService) {
        super(authenticationManager);
        this.authenticationService = authenticationService;
        setTargetMethods();
    }

    /**
     * 필터가 적용될 메서드를 설정한다.
     */
    private void setTargetMethods() {
        targetMethods.add(HttpMethod.POST);
        targetMethods.add(HttpMethod.PATCH);
        targetMethods.add(HttpMethod.DELETE);
        targetMethods.add(HttpMethod.PUT);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String authorization = request.getHeader("Authorization");

        if(hasText(authorization)) {
            authorize(authorization);
        }

        super.doFilterInternal(request, response, chain);
    }

    private void authorize(String authorization) {
        String token = authorization.substring("Bearer ".length());
        Long userId = authenticationService.parseToken(token);
        Authentication authentication = new UserAuthentication(userId);
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(authentication);
    }
}
