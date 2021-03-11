package com.codesoom.assignment.filters;

import com.codesoom.assignment.application.AuthenticationService;
import com.codesoom.assignment.errors.InvalidTokenException;
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
 * JWT 인증 필터.
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

        String authorization = request.getHeader("Authorization");

        if (authorization == null) {
            throw new InvalidTokenException("");
        }

        String accessToken = authorization.substring("Bearer ".length());

        Long userId = authenticationService.parseToken(accessToken);

        Authentication authentication = null;
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(authentication);

        request.setAttribute("userId", userId);

        chain.doFilter(request, response);
    }

    /**
     * 경로와 http method를 기준으로 필터한다.
     *
     * @param request
     * @return boolean 필터 후 참/거짓 값
     */
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
}
