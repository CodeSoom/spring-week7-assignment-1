package com.codesoom.assignment.security.jwt;

import com.codesoom.assignment.common.message.ErrorMessage;
import com.codesoom.assignment.errors.authentication.InvalidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
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


    private void isSameAs(Claims token, String accessUser) {
        String tokenGeneratedUser = jwtParser.getUserEmailFromToken(token);
        boolean isEqual = tokenGeneratedUser.equals(accessUser);
        if (!isEqual) {
            throw new InvalidTokenException(ErrorMessage.INVALID_USER_BY_TOKEN.getErrorMsg());
        }
    }


    public void validateToken(String token, String assessUserEmail) {
        try {
            Claims tokenBodies = jwtParser.getTokenBodies(token);
            isSameAs(tokenBodies, assessUserEmail);
        } catch (ExpiredJwtException e) {
            throw new InvalidTokenException(ErrorMessage.EXPIRED_TOKEN.getErrorMsg());
        } catch (IllegalArgumentException e) {
            throw new InvalidTokenException(ErrorMessage.INVALID_TOKEN.getErrorMsg());
        } catch (SignatureException e) {
            throw new InvalidTokenException(ErrorMessage.INVALID_USER_BY_TOKEN.getErrorMsg());
        }

    }
}
