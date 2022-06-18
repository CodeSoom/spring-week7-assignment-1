package com.codesoom.assignment.filter;

import com.codesoom.assignment.application.AuthenticationService;
import com.codesoom.assignment.domain.User;
import com.codesoom.assignment.domain.UserRepository;
import com.codesoom.assignment.errors.InvalidTokenException;
import com.codesoom.assignment.errors.UserNotFoundException;
import com.codesoom.assignment.security.UserAuthentication;
import org.springframework.security.authentication.AuthenticationManager;
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
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager,
                                   AuthenticationService authenticationService, UserRepository userRepository) {
        super(authenticationManager);
        this.authenticationService = authenticationService;
        this.userRepository = userRepository;
    }

    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws
            IOException,
            ServletException {

        String authorization = request.getHeader("Authorization");

        if (authorization != null) {
            try {
                String accessToken = authorization.substring("Bearer ".length());
                Long userId = authenticationService.parseToken(accessToken);
                User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
                UserAuthentication authentication = new UserAuthentication(user);

                final SecurityContext context = SecurityContextHolder.getContext();
                context.setAuthentication(authentication);
            } catch (Exception e) {
                throw new InvalidTokenException(authorization);
            }
        }
        chain.doFilter(request, response);
    }
}
