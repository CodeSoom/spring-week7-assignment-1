package com.codesoom.assignment.security.jwt;

import com.codesoom.assignment.common.message.ErrorMessage;
import com.codesoom.assignment.errors.CustomInternalServerException;
import io.jsonwebtoken.Jwts;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtGenerator {
    private JwtConfig jwtConfig;

    public JwtGenerator(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    public String generateToken(String userEmail) {

        if (StringUtils.isEmpty(userEmail)) {
            throw new CustomInternalServerException(ErrorMessage.EMPTY_USER_EMAIL_TOKEN);
        }

        long expireDateLong = Long.sum(jwtConfig.getJwtExpireMs(), System.currentTimeMillis());
        Date expiryDate = new Date(expireDateLong);

        return Jwts.builder()
                .claim(jwtConfig.getClaimKey(), userEmail)
                .setExpiration(expiryDate)
                .signWith(jwtConfig.getSecretKey())
                .compact();
    }
}
