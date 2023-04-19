package com.codesoom.assignment.filter;

import com.codesoom.assignment.application.AuthenticationService;
import com.codesoom.assignment.domain.UserType;
import com.codesoom.assignment.dto.ClaimData;
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
        String authorization = request.getHeader("Authorization");

        if (authorization != null) {
            String accessToken = authorization.substring("Bearer ".length());
            ClaimData claimData = authenticationService.parseToken(accessToken);
            Long userId = claimData.getUserId();
            UserType userType = claimData.getUserType();
            request.setAttribute("userId", userId);

            // 인증
            Authentication authentication = new UserAuthentication(userId,userType);
            SecurityContext context = SecurityContextHolder.getContext();
            context.setAuthentication(authentication);
        }

        // 다음 필터에게 전달한다
        chain.doFilter(request, response);
    }
}
