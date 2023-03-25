package com.codesoom.assignment.utils;

import com.codesoom.assignment.domain.User;
import com.codesoom.assignment.errors.InvalidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtUtilTest {
    private static final String SECRET = "12345678901234567890123456789012";

    private static final String validToken = "eyJhbGciOiJIUzI1NiJ9." +
            "eyJ1c2VySWQiOjEsInJvbGUiOiJST0xFX1VTRVIiLCJleHAiOjE2ODA5MjU0MzZ9.oQ2Iolzi0Waj9v5LafnIhL-atlbaPf-1fma34XQYXMY";
    private static final String invalidToken = "eyJhbGciOiJIUzI1NiJ9." +
            "eyJ1c2VySWQiOjEsInJvbGUiOiJST0xFX1VTRVIiLCJleHAiOjE2ODA5MjUyMzZ9.XWwkC7wOcY1yAhIbcKMAbtoIui2980dxVRcq-mR90Eg_INVALID";

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil(SECRET);
    }


    @Test
    void decodeWithValidToken() {
        Claims claims = jwtUtil.decode(validToken);

        assertThat(claims.get("userId", Long.class)).isEqualTo(1L);
    }

    @Test
    void decodeWithInvalidToken() {
        assertThatThrownBy(() -> jwtUtil.decode(invalidToken))
                .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    void decodeWithEmptyToken() {
        assertThatThrownBy(() -> jwtUtil.decode(null))
                .isInstanceOf(InvalidTokenException.class);

        assertThatThrownBy(() -> jwtUtil.decode(""))
                .isInstanceOf(InvalidTokenException.class);

        assertThatThrownBy(() -> jwtUtil.decode("   "))
                .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    void createRefreshToken(){

        User user = User.builder()
                .id(1L)
                .name("테스터")
                .role("ROLE_USER")
                .build();

        assertThat(jwtUtil.createRefreshToken(user)).contains(".");
    }

    @Test
    void createAccessToken(){
        User user = User.builder()
                .id(1L)
                .name("테스터")
                .role("ROLE_USER")
                .build();
        assertThat(jwtUtil.createAccessToken(user)).contains(".");
    }
}
