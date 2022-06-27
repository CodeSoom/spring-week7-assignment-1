package com.codesoom.assignment.utils;

import com.codesoom.assignment.domain.User;
import com.codesoom.assignment.errors.InvalidTokenException;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtUtilTest {
    private static final String SECRET = "12345678901234567890123456789012";

    private static final String VALID_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjEsInJvbGUiOiJST0xFX1VTRVIifQ." +
            "SgLtYfTVUdvPF-gIP006U-_B7-wWMSUcJD3eoSOxHsE";
    private static final String INVALID_TOKEN = VALID_TOKEN + "WRONG";

    private JwtUtil jwtUtil;

    private User user;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil(SECRET);

        user = User.builder()
                .id(1L)
                .name("name")
                .password("12345678")
                .build();
    }

    @Test
    void encode() {
        String token = jwtUtil.encode(user);

        assertThat(token).isEqualTo(VALID_TOKEN);
    }

    @Test
    void decodeWithValidToken() {
        Claims claims = jwtUtil.decode(VALID_TOKEN);

        assertThat(claims.get("userId", Long.class)).isEqualTo(user.getId());
        assertThat(claims.get("role")).isEqualTo(user.getRole().getName());
    }

    @Test
    void decodeWithInvalidToken() {
        assertThatThrownBy(() -> jwtUtil.decode(INVALID_TOKEN))
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
}
