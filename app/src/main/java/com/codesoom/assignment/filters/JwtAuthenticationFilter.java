package com.codesoom.assignment.filters;

import com.codesoom.assignment.application.AuthenticationService;
import com.codesoom.assignment.errors.InvalidTokenException;
import com.codesoom.assignment.security.UserAuthentication;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * JWT를 분석하는 Spring Security Filter이다.
 *
 * {@link org.springframework.web.filter.OncePerRequestFilter#doFilterInternal(HttpServletRequest, HttpServletResponse, FilterChain)} 메소드를 재정의 하여
 * 서블릿 필터에서 JWT의 payload를 분석하여 {@link org.springframework.security.core.context.SecurityContextHolder}에 넣는다.
 *
 * @throws InvalidTokenException JWT 분석 실패할 경우
 */
public class JwtAuthenticationFilter extends BasicAuthenticationFilter {

    private final AuthenticationService authenticationService;
    private final Long ADMIN_ID = -1L;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, AuthenticationService authenticationService) {
        super(authenticationManager);
        this.authenticationService = authenticationService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        if(filterWithPathAndMethod(request)){
            chain.doFilter(request , response);
            return;
        }

        String authorization = request.getHeader("Authorization");

        if (authorization != null) {
            String accessToken = authorization.substring("Bearer ".length());
            Long userId = authenticationService.parseToken(accessToken);
            Authentication authentication = new UserAuthentication(userId , authorities(userId));

            SecurityContext context = SecurityContextHolder.getContext();
            context.setAuthentication(authentication);
        }

        chain.doFilter(request , response);
    }

    private List<GrantedAuthority> authorities(Long userId) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        if(Objects.equals(userId, ADMIN_ID)){
            authorities.add(new SimpleGrantedAuthority("ADMIN"));
        }
        return authorities;
    }

    private boolean filterWithPathAndMethod(HttpServletRequest request) {
        String path = request.getRequestURI();
        if (path.startsWith("/session")) {
            return true;
        }

        HttpMethod method = HttpMethod.resolve(request.getMethod());
        return method == null || method.matches("GET") || method.matches("OPTIONS");
    }
}
