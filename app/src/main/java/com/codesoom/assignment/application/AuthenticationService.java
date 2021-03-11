package com.codesoom.assignment.application;

import com.codesoom.assignment.domain.User;
import com.codesoom.assignment.domain.UserRepository;
import com.codesoom.assignment.errors.LoginFailException;
import com.codesoom.assignment.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Service;

/**
 * 유저 인증에 대한 기능을 제공.
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
     * 주어진 유저 로그인 정보로 인증하여 발급된 Token 값을 반환한다.
     *
     * @param email    유저 이메일
     * @param password 유저 비밀번호
     * @return Token 값
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
     * 주어진 Token 값으로 부터 유저 식별자를 반환한다.
     *
     * @param accessToken Token 값
     * @return 유저 식별자
     */
    public Long parseToken(String accessToken) {
        Claims claims = jwtUtil.decode(accessToken);
        return claims.get("userId", Long.class);
    }
}
