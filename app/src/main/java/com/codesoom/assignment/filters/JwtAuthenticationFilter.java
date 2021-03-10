package com.codesoom.assignment.filters;

import com.codesoom.assignment.application.AuthenticationService;
import com.codesoom.assignment.errors.InvalidTokenException;
import com.codesoom.assignment.security.UserAuthentication;
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

        // /products, GET, OPTIONS는 filter에서 제외되게 처리.
        // TODO : 이부분을 지우고 싶다.
//        if (filterWithPathAndMethod(request)) {
//            chain.doFilter(request, response);
//            return;
//        }

        // TODO : authentication check
        String authorization = request.getHeader("Authorization");

        // Token does not exist in the header (401 error)
        // * null이 아닐때로 만들자!
//        if (authorization == null) {
//            throw new InvalidTokenException("");
//            response.sendError(HttpStatus.UNAUTHORIZED.value());
//            return;
//        }

        if (authorization != null) {
            // Authorization token 값 얻기
            String accessToken = authorization.substring("Bearer ".length());

            Long userId = authenticationService.parseToken(accessToken);
            // TODO : userID를 넘겨주자 => authentication

            Authentication authentication = new UserAuthentication(userId);

            SecurityContext context = SecurityContextHolder.getContext();
            context.setAuthentication(authentication);
        }

        chain.doFilter(request, response);
    }

//    private boolean filterWithPathAndMethod(HttpServletRequest request) {
//        String path = request.getRequestURI();
//        if (!path.startsWith("/products")) {
//            return true;
//        }
//
//        String method = request.getMethod();
//        if (method.equals("GET")) {
//            return true;
//        }
//
//        if (method.equals("OPTIONS")) {
//            return true;
//        }
//
//        return false;
//    }
}
