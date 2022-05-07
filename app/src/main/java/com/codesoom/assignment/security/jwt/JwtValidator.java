package com.codesoom.assignment.security.jwt;

import com.codesoom.assignment.common.message.ErrorMessage;
import com.codesoom.assignment.errors.authentication.InvalidTokenException;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Component;

@Component
public class JwtValidator {
    private final JwtConfig jwtConfig;
    private final JwtParser jwtParser;

    public JwtValidator(JwtConfig jwtConfig,
                        JwtParser jwtParser) {
        this.jwtConfig = jwtConfig;
        this.jwtParser = jwtParser;
    }

    public String getUserEmailFromToken(Claims token) {
        return String.valueOf(token.get(jwtConfig.getClaimKey()));
    }

    public String getUserEmailFromToken(String token) {
        Claims body = jwtParser.getTokenBodies(token);
        return String.valueOf(body.get(jwtConfig.getClaimKey()));
    }

    private void isExpiredToken(Claims bodies) {
        long expirationMs = bodies.getExpiration().getTime();
        long currentMs = System.currentTimeMillis();

        long diffExpirationToCurrent = Math.subtractExact(expirationMs, currentMs);

        if (diffExpirationToCurrent < 0) {
            throw new InvalidTokenException(ErrorMessage.EXPIRED_TOKEN.getErrorMsg());
        }

    }

    private void isSameAs(Claims token, String accessUser) {
        String tokenGeneratedUser = getUserEmailFromToken(token);
        boolean isEqual = tokenGeneratedUser.equals(accessUser);
        if (!isEqual) {
            throw new InvalidTokenException(ErrorMessage.INVALID_USER_BY_TOKEN.getErrorMsg());
        }
    }


    public void validateToken(String token, String assesUserEmail) {
        Claims tokenBody = jwtParser.getTokenBodies(token);
        isExpiredToken(tokenBody);
        isSameAs(tokenBody, assesUserEmail);
    }
}
