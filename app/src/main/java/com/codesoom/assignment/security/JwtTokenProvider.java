package com.codesoom.assignment.security;

import com.codesoom.assignment.application.UserService;
import com.codesoom.assignment.errors.InvalidTokenException;
import com.codesoom.assignment.security.UserAuthentication;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.security.Key;

@Component
public class JwtTokenProvider {
    private static final Long TOKEN_VALID_TIME = 1000L * 60 * 60; //60 분

    private final Key key;

    private final UserService userService;

    public JwtTokenProvider(@Value("${jwt.secret}") String secret, UserService userService) {
        key = Keys.hmacShaKeyFor(secret.getBytes());
        this.userService = userService;
    }

    public String createToken(Long userId) {
        return Jwts.builder()
                .claim("userId", userId)
                .signWith(key)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        return new UserAuthentication(getUserId(token));
    }

    public Long getUserId(String token) {
        if (token == null || token.isBlank()) {
            throw new InvalidTokenException(token);
        }

        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get("userId", Long.class);

        } catch (SignatureException e) {
            throw new InvalidTokenException(token);
        }
    }

    public String getHeader(HttpServletRequest request) {
        return request.getHeader("Authorization");
    }
}
