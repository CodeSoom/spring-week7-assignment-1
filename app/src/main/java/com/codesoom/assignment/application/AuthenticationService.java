package com.codesoom.assignment.application;

import com.codesoom.assignment.domain.UserRepository;
import com.codesoom.assignment.errors.InvalidTokenException;
import com.codesoom.assignment.errors.LoginFailException;
import com.codesoom.assignment.utils.JwtUtil;
import io.jsonwebtoken.RequiredTypeException;
import org.springframework.stereotype.Service;

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
     * 회원 로그인을 처리하고 토큰을 반환합니다.
     * @param email 로그인할 이메일
     * @param password 비밀번호
     * @return 토큰
     * @throws LoginFailException 회원을 찾지 못하거나 비밀번호가 틀린 경우
     */
    public String login(String email, String password) {
        return userRepository.findByEmail(email)
                .map(user -> {
                    if (!user.authenticate(password)) {
                        return null;
                    }
                    return jwtUtil.encode(user.getId());
                }).orElseThrow(() -> new LoginFailException(email));
    }

    /**
     * 토큰에서 회원 정보를 꺼냅니다.
     * @param accessToken 토큰
     * @return 회원 정보
     * @throws InvalidTokenException 토큰의 클레임에서 회원 정보를 꺼낼 수 없는 경우
     */
    public Long parseToken(String accessToken) {
        try {
            return jwtUtil.decode(accessToken)
                    .get("userId", Long.class);
        } catch (RequiredTypeException e) {
            throw new InvalidTokenException("토큰의 클레임 값이 잘못되었습니다.");
        }
    }
}
