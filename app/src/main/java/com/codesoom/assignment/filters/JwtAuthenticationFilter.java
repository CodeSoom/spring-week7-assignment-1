package com.codesoom.assignment.filters;

import com.codesoom.assignment.security.UserAuthentication;
import com.codesoom.assignment.utils.JwtUtil;
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
    private JwtUtil jwtUtil;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        super(authenticationManager);
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        String authorization = request.getHeader("Authorization");

        if (authorization != null) {
            String accessToken = authorization.substring("Bearer ".length());
            Long userId = jwtUtil.decode(accessToken)
                    .get("userId", Long.class);
            Authentication authentication = new UserAuthentication(userId);


            SecurityContext context = SecurityContextHolder.getContext();
            context.setAuthentication(authentication);
        }

        chain.doFilter(request, response);

    }

}
