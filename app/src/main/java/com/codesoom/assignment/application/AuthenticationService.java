package com.codesoom.assignment.application;

import com.codesoom.assignment.domain.User;
import com.codesoom.assignment.domain.UserRepository;
import com.codesoom.assignment.errors.LoginFailException;
import com.codesoom.assignment.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 회원 인증 로직을 처리합니다.
 */
@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationService(UserRepository userRepository,
                                 JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 검증된 회원이면 access token을 반환합니다.
     * @param email 회원의 이메일
     * @param password 회원의 비밀번호
     * @return JWT Access Token
     * @throws LoginFailException 로그인정보가 유효하지 않은 경우
     */
    public String login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new LoginFailException(email));

        if (!user.authenticate(password, passwordEncoder)) {
            throw new LoginFailException(email);
        }

        return jwtUtil.encode(user.getId());
    }

    /**
     * 엑세스 토큰에서 회원의 id를 반환합니다.
     * @param accessToken 액세스 토큰
     * @return 회원의 id
     */
    public Long parseToken(String accessToken) {
        Claims claims = jwtUtil.decode(accessToken);
        return claims.get("userId", Long.class);
    }
}
