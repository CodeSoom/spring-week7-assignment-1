package com.codesoom.assignment.filters;

import com.codesoom.assignment.application.AuthenticationService;
import com.codesoom.assignment.errors.InvalidTokenException;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

        authenticate(request);

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

    /**
     * 요청 정보를 통해 인증이 되었는지 확인합니다.
     * @param request 요청 정보
     * @throws InvalidTokenException 토큰이 올바르지 않은 경우
     */
    private void authenticate(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");

        if (authorization == null) {
            throw new InvalidTokenException("");
        }

        String accessToken = getAccessToken(authorization);
        Long userId = authenticationService.parseToken(accessToken);

        request.setAttribute("userId", userId);
    }

    private String getAccessToken(String authorization) {
        return authorization.substring("Bearer ".length());
    }
}
