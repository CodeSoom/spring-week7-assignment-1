package com.codesoom.assignment.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

@Component
public class JwtParser {
    private JwtConfig jwtConfig;

    public JwtParser(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    private Jws<Claims> parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(jwtConfig.getSecretKey())
                .build()
                .parseClaimsJws(token);
    }

    public Claims getTokenBodies(String token) {
        return parseToken(token).getBody();
    }

    public Jws<Claims> getParseTokenInfo(String token) {
        return parseToken(token);
    }

    public String getUserEmailFromToken(Claims token) {
        return String.valueOf(token.get(jwtConfig.getClaimKey()));
    }

    public String getUserEmailFromToken(String token) {
        Claims body = getTokenBodies(token);
        return String.valueOf(body.get(jwtConfig.getClaimKey()));
    }


}
