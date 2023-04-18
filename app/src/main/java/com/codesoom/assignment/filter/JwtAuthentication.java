package com.codesoom.assignment.filter;

import com.codesoom.assignment.application.AuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * Filter 를 대신 BasicAuthenticationFilter 사용
 * 기본적인 내용을 구성해놨음
 */
public class JwtAuthentication extends BasicAuthenticationFilter {
    private final AuthenticationService authenticationService;

    public JwtAuthentication(AuthenticationManager authenticationManager, AuthenticationService authenticationService) {
        super(authenticationManager);
        this.authenticationService = authenticationService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (filterWithPathAndMethod(request)) {
            chain.doFilter(request, response);
            return;
        }

        //TODO authentication
        String authorization = request.getHeader("Authorization");

        if (authorization == null) {
            //TODO EXCEPTION 으로 변경
            response.sendError(HttpStatus.UNAUTHORIZED.value());
            return;
        }

        // 토큰 생성
        String accessToken = authorization.substring("Bearer ".length());
        Long userId = authenticationService.parseToken(accessToken);
        request.setAttribute("userId", userId);

        // 다음 필터에게 전달한다
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

}
