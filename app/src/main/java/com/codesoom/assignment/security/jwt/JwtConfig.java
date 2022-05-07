package com.codesoom.assignment.security.jwt;

import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;

/**
 *  토큰 설정을 책임집니다
 */
@Getter
@Component
public class JwtConfig {

    private final Key secretKey;
    private final int jwtExpireMs;
    private final String claimKey;

    public JwtConfig(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.claimKey}") String claimKey,
            @Value("${jwt.expireMs}") int jwtExpireMs
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes());
        this.jwtExpireMs = jwtExpireMs;
        this.claimKey = claimKey;
    }
}
