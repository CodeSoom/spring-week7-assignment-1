package com.codesoom.assignment.filters;

import com.codesoom.assignment.errors.InvalidTokenException;
import org.springframework.http.HttpStatus;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 인증 에러를 처리한다.
 */
public class AuthenticationErrorFilter extends HttpFilter {

    /**
     * 요청과 응답 쌍을 chain을 통해 전달한다.
     *
     * @param request - 요청
     * @param response - 응답
     * @param chain - 요청과 응답 쌍을 Chain 내 다음 Entity로 전달
     * @throws IOException - I/O 명령이 실패한 경우
     * @throws ServletException - Servlet 에러가 발생한 경우
     * @throws InvalidTokenException - 토큰이 유효하지 않은 경우
     */
    @Override
    protected void doFilter(HttpServletRequest request,
                            HttpServletResponse response,
                            FilterChain chain)
        throws IOException, ServletException, InvalidTokenException {
        try {
            chain.doFilter(request, response);
        } catch (InvalidTokenException e) {
            response.sendError(HttpStatus.UNAUTHORIZED.value());
        }
    }
}
