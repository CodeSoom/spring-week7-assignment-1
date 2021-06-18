package com.codesoom.assignment.filters;

import com.codesoom.assignment.application.AuthenticationService;
import com.codesoom.assignment.domain.User;
import com.codesoom.assignment.security.UserAuthentication;
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

/**
 * 권한이 필요한 API 요청 시 토큰을 검증하는 필터
 */
public class JwtAuthenticationFilter extends BasicAuthenticationFilter {
    private final AuthenticationService authenticationService;

    public JwtAuthenticationFilter(
            AuthenticationManager authenticationManager,
            AuthenticationService authenticationService) {
        super(authenticationManager);
        this.authenticationService = authenticationService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws IOException, ServletException {

        if (filterWithPathAndMethod(request)) {
            chain.doFilter(request, response);
            return;
        }

        setSecurityContext(request);

        chain.doFilter(request, response);
    }

    private void setSecurityContext(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (authorization == null) {
            return;
        }

        String accessToken = authorization.substring("Bearer ".length());
        Long userId = authenticationService.parseToken(accessToken);
        User user = User.builder().id(userId).build();
        Authentication authentication = new UserAuthentication(user);

        SecurityContext context = SecurityContextHolder.getContext();
        if (context.getAuthentication() == null
                || !context.getAuthentication()
                           .isAuthenticated()) {
            // Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead
            // authentication.setAuthenticated(true);
            context.setAuthentication(authentication);
        }
    }

    private boolean filterWithPathAndMethod(HttpServletRequest request) {
        String path = request.getRequestURI();
        if (!path.startsWith("/products")
                && !path.startsWith("/users")) {
            return true;
        }

        String method = request.getMethod();
        if (method.equals(HttpMethod.GET.name())) {
            return true;
        }

        if (method.equals(HttpMethod.OPTIONS.name())) {
            return true;
        }

        return false;
    }
}
