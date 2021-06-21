package com.codesoom.assignment.utils;

import com.codesoom.assignment.errors.InvalidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;

/**
 * 토큰(JWT, JSON Web Token)을 발급하고 검증하는 유틸리티 클래스
 *
 * @see <a href="https://tools.ietf.org/html/rfc7519">RFC7519 - JSON Web Token (JWT)</a>
 */
@Component
public class JwtUtil {
    private final Key key;

    public JwtUtil(@Value("${jwt.secret}") String secret) {
        key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * 토큰을 발급합니다.
     *
     * @param userId 사용자 식별자
     * @return 토큰
     */
    public String encode(Long userId) {
        return Jwts.builder()
                .claim("userId", userId)
                .signWith(key)
                .compact();
    }

    /**
     * 토큰을 해석하고, 유효하다면 토큰에 포함된 정보를 리턴합니다.
     *
     * @param token 토큰
     * @return 토큰에 포함된 정보
     * @throws InvalidTokenException 토큰이 빈 값이거나 시그니처가 올바르지 않을 경우
     */
    public Claims decode(String token) {
        if (token == null || token.isBlank()) {
            throw new InvalidTokenException(token);
        }

        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (SignatureException e) {
            throw new InvalidTokenException(token);
        }
    }
}
