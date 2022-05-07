package com.codesoom.assignment.security.jwt;

import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtGenerator {
    private JwtConfig jwtConfig;

    public JwtGenerator(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    public String generateToken(String userEmail) {

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtConfig.getJwtExpireMs());

        return Jwts.builder()
                .claim(jwtConfig.getClaimKey(), userEmail)
                .setExpiration(expiryDate)
                .signWith(jwtConfig.getSecretKey())
                .compact();
    }
}
