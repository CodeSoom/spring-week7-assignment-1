package com.codesoom.assignment.filter;

import com.codesoom.assignment.application.AuthenticationService;
import com.codesoom.assignment.errors.InvalidTokenException;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JWT 인가 필터
 */
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private final AuthenticationService authenticationService;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, AuthenticationService authenticationService) {
        super(authenticationManager);
        this.authenticationService = authenticationService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws IOException, ServletException {

        if (!isAuthenticationRequired(request)) {
            chain.doFilter(request, response);
            return;
        }

        final String authorization = request.getHeader("Authorization");

        if (authorization == null) {
            throw new InvalidTokenException("HTTP 요청 헤더에 Authorization 이 있어야 합니다.");
        }

        final String accessToken = authorization.substring("Bearer ".length());
        authenticationService.parseToken(accessToken);

        chain.doFilter(request, response);
    }

    /**
     * 인증이 필요한 HTTP 요청인지 확인합니다.<br>
     * 인증이 필요한 요청이면 true 를 리턴하고, 인증이 필요하지 않다면 false 를 리턴합니다.<br><br>
     *
     * @param httpRequest HTTP 요청 정보
     * @return 인증이 필요한 요청이면 true, 필요하지 않다면 false
     */
    private boolean isAuthenticationRequired(HttpServletRequest httpRequest) {

        final String path = httpRequest.getRequestURI();
        final String method = httpRequest.getMethod();

        if (path.startsWith("/products") && HttpMethod.GET.matches(method)) {
            return false;
        }

        if ("/users".equals(path) && HttpMethod.POST.matches(method)) {
            return false;
        }

        if ("/session" .equals(path)) {
            return false;
        }
        return true;
    }
}
