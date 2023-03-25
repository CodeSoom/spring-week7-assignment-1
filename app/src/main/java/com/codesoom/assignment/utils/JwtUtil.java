package com.codesoom.assignment.utils;

import com.codesoom.assignment.domain.User;
import com.codesoom.assignment.errors.InvalidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
    private final Key key;

    public JwtUtil(@Value("${jwt.secret}") String secret) {
        key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    private final int DAY_TIME = 1000*60*60*24;

    private final int HOUR_TIME = 1000*60*60;

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

    public String createRefreshToken(User user) {
        Date now = new Date();

        return Jwts.builder()
                .claim("userId", user.getId())
                .claim("role", user.getRole())
                .setExpiration(new Date(now.getTime() + DAY_TIME*14))
                .signWith(key)
                .compact();
    }

    public String createAccessToken(User user) {
        Date now = new Date();

        return Jwts.builder()
                .claim("userId", user.getId())
                .claim("role", user.getRole())
                .setExpiration(new Date(now.getTime() + HOUR_TIME))
                .signWith(key)
                .compact();
    }
}
