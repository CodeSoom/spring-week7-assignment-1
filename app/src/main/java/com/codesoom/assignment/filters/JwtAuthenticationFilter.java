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

/**
 * Jwt가 유효한 토큰인지 인증하기 위한 필터
 */
public class JwtAuthenticationFilter extends BasicAuthenticationFilter {
    private final AuthenticationService authenticationService;

    // authenticationManager : Spring Security의 필터들이 인증을 수행하는 방법에 대한 명세를 정의해 놓은 인터페이스
    public JwtAuthenticationFilter(
            AuthenticationManager authenticationManager,
            AuthenticationService authenticationService) {
        super(authenticationManager);
        this.authenticationService = authenticationService;
    }

    /**
     * 주어진 요청으로 유저 인증을 수행합니다.
     *
     * @param request  요청 정보
     * @param response 응답 정보
     * @param chain    필터 체인
     * @throws IOException
     * @throws ServletException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws IOException, ServletException {

        // 헤더에서 jwt를 받아온다.
        String authorization = request.getHeader("Authorization");

        if (authorization != null) {

            // 토큰만 분리해낸다.
            String accessToken = authorization.substring("Bearer ".length());

            // parseToken 메서드로 주어진 토큰에서 회원 식별자를 추출하여 리턴한다.
            Long userId = authenticationService.parseToken(accessToken);

            // JwtAuthenticationFilter에서 인증이 완료되면 UserAuthentication 객체가 생성되고 SecurityContext에 보관한다.
            Authentication authentication = new UserAuthentication(userId);
            SecurityContext context = SecurityContextHolder.getContext(); // SecurityContextHolder : Spring Security의 인메모리 세션저장소
            context.setAuthentication(authentication);
        }
        // 체인의 다음 필터를 처리한다.
        chain.doFilter(request, response);
    }
}
