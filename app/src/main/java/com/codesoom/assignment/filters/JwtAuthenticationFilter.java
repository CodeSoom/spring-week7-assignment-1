package com.codesoom.assignment.filters;

import com.codesoom.assignment.application.AuthenticationService;
import com.codesoom.assignment.authentication.UserAuthentication;
import com.codesoom.assignment.errors.InvalidTokenException;
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
import java.util.Set;

import static org.springframework.util.StringUtils.isEmpty;

public class JwtAuthenticationFilter extends BasicAuthenticationFilter {
    private final AuthenticationService authenticationService;
    private final Set<String> userPathTargetMethods = Set.of(HttpMethod.PATCH.name(), HttpMethod.DELETE.name(), HttpMethod.PUT.name());
    private final Set<String> productPathTargetMethods = Set.of(HttpMethod.POST.name(), HttpMethod.PATCH.name(), HttpMethod.DELETE.name(), HttpMethod.PUT.name());

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, AuthenticationService authenticationService) {
        super(authenticationManager);
        this.authenticationService = authenticationService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        if((requestURI.startsWith("/users") && userPathTargetMethods.contains(method))
                || (requestURI.startsWith("/products") && productPathTargetMethods.contains(method))
        ) {
            String authorization = request.getHeader("Authorization");
            authorize(authorization);
        }

        super.doFilterInternal(request, response, chain);
    }

    private void authorize(String authorization) {
        if(isEmpty(authorization)) {
            throw new InvalidTokenException("토큰이 없습니다.");
        }

        String token = authorization.substring("Bearer ".length());
        Long userId = authenticationService.parseToken(token);
        Authentication authentication = new UserAuthentication(userId);
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(authentication);
    }
}
