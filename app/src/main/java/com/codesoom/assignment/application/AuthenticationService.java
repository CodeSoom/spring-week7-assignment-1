package com.codesoom.assignment.application;

import com.codesoom.assignment.application.dto.SessionCommand;
import com.codesoom.assignment.domain.User;
import com.codesoom.assignment.domain.UserRepository;
import com.codesoom.assignment.errors.LoginFailException;
import com.codesoom.assignment.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationService(UserRepository userRepository, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 로그인 정보로 인증 후 인증토큰을 리턴한다.
     * @param command 로그인 정보
     * @return 인증토큰
     * @throws LoginFailException 로그인 인증에 실패한 경우
     */
    public String login(SessionCommand.SessionRequest command) {
        final User user = userRepository.findByEmail(command.getEmail())
                .orElseThrow(() -> new LoginFailException(command.getEmail()));

        if (!user.authenticate(command.getPassword(), passwordEncoder)) {
            throw new LoginFailException(command.getEmail());
        }

        return jwtUtil.encode(user.getId());
    }

    /**
     * 인증토큰을 검증 후 복호화된 사용자 ID를 리턴한다.
     * @param accessToken 인증토큰
     * @return 사용자 ID
     */
    public Long parseToken(String accessToken) {
        final Claims claims = jwtUtil.decode(accessToken);
        return claims.get("userId", Long.class);
    }
}
