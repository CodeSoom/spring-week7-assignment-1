package com.codesoom.assignment.application.auth;

import com.codesoom.assignment.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Service;

@Service
public class UserAuthorizationService implements AuthorizationService {

    private final JwtUtil jwtUtil;

    public UserAuthorizationService(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Long parseToken(String accessToken) {
        final Claims claims = jwtUtil.decode(accessToken);
        final Long userId = claims.get("userId", Long.class);

        return userId;
    }

}
