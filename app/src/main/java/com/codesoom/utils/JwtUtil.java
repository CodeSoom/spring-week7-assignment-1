package com.codesoom.utils;

import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;

/**
 * 토큰 관련 처리를 하는 클래스
 */
@Component
public class JwtUtil {
    private final Key key;

    public JwtUtil(@Value("${jwt.secret}") String secret) {
        key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    // TODO: 토큰을 반환해야 한다.
    public String encodeUserId(Long userId) {
        return null;
    }
}
