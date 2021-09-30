package com.codesoom.assignment.filters;

import com.codesoom.assignment.application.AuthenticationService;
import com.codesoom.assignment.security.UserAuthentication;
import org.springframework.http.HttpStatus;
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

public class JwtAuthenticationFilter extends BasicAuthenticationFilter {
    private final AuthenticationService authenticationService;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, AuthenticationService authenticationService) {
        super(authenticationManager);
        this.authenticationService = authenticationService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws IOException, ServletException {
        String authorization = request.getHeader("Authorization");

        if (authorization != null) {
            String accessToken = authorization.substring("Bearer ".length());
            Long userId = authenticationService.parseToken(accessToken);
            Authentication authentication = new UserAuthentication(userId);

            SecurityContext context = SecurityContextHolder.getContext();
            context.setAuthentication(authentication);
        }

        // interceptor의 preHandle(true, false를 리턴)과 유사
        // 다음 필터를 호출
        chain.doFilter(request, response);
    }


    // (기존 코드)
    /* ------------------------------------------------------------------------------------------------------------------------------ */

    // 필터에서 접근에 대한 종합적인 처리를 할 때,
    // doFilterInternal
        /* Todo : 이 부분을 지우고 싶다. => 컨트롤러에 직점 메서드 시큐리티 적용
        if (filterWithPathAndMethod(request)) {
            chain.doFilter(request, response);
            return;
        }
        */
    // Exception으로 변경할 것
    // if (authorization == null) {
    //response.sendError(HttpStatus.UNAUTHORIZED.value());
    //return;

    // 사용 안 함. => 컨트롤러에 직접 메서드 시큐리티 적용
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
