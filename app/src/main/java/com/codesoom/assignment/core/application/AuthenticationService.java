package com.codesoom.assignment.core.application;

import com.codesoom.assignment.core.domain.User;
import com.codesoom.assignment.core.domain.UserRepository;
import com.codesoom.assignment.error.LoginFailException;
import com.codesoom.assignment.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Service;

/**
 * 로그인 인증토큰(Access token)를 가공하여 반환하거나 처리합니다.
 */
@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public AuthenticationService(UserRepository userRepository,
                                 JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    /**
     * 로그인 인증토큰을 발급하고 반환합니다.
     * @return 로그인 인증토큰
     */
    public String login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new LoginFailException(email));

        if (!user.authenticate(password)) {
            throw new LoginFailException(email);
        }

        return jwtUtil.encode(user.getId());
    }

    /**
     * 로그인 인증토큰을 검증하고 회원 식별자를 반환합니다.
     * @param accessToken 인증토큰
     * @return 회원 식별자
     */
    public Long parseToken(String accessToken) {
        Claims claims = jwtUtil.decode(accessToken);
        return claims.get("userId", Long.class);
    }
}
