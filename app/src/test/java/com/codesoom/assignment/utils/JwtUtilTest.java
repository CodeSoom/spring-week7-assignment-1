package com.codesoom.assignment.utils;

import com.codesoom.assignment.errors.InvalidTokenException;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtUtilTest {
    private static final String SECRET = "12345678901234567890123456789012";

    private static final String VALID_TOKEN = "eyJhbGciOiJIUzI1NiJ9." +
            "eyJ1c2VySWQiOjF9.ZZ3CUl0jxeLGvQ1Js5nG2Ty5qGTlqai5ubDMXZOdaDk";
    private static final String INVALID_TOKEN = "eyJhbGciOiJIUzI1NiJ9." +
            "eyJ1c2VySWQiOjF9.ZZ3CUl0jxeLGvQ1Js5nG2Ty5qGTlqai5ubDMXZOdaD0";

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil(SECRET);
    }

    @Test
    void encode() {
        String token = jwtUtil.encode(1L);

        assertThat(token).isEqualTo(VALID_TOKEN);
    }

    @Test
    void decodeWithValidToken() {
        Claims claims = jwtUtil.decode(VALID_TOKEN);

        assertThat(claims.get("userId", Long.class)).isEqualTo(1L);
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

    @Nested
    @DisplayName("validateToken 메소드는")
    class Describe_validateToken {

        @Nested
        @DisplayName("유효한 토큰이 주어지면")
        class Context_with_valid_token {
            private String token;

            @BeforeEach
            void setUp() {
                token = jwtUtil.encode(1L);
            }

            @Test
            @DisplayName("true를 반환한다")
            void it_returns_true() {
                assertThat(jwtUtil.validateToken(token)).isTrue();
            }
        }

        @Nested
        @DisplayName("null이나 공백이 주어지면")
        class Context_with_null_or_white_space {

            @ParameterizedTest
            @NullAndEmptySource
            @ValueSource(strings = {"\n", "\t", " "})
            @DisplayName("false를 반환한다")
            void it_returns_false(String token) {
                assertThat(jwtUtil.validateToken(token)).isFalse();
            }
        }

        @Nested
        @DisplayName("토큰으로 해석할 수 없는 문자열이 들어오면")
        class Context_with_wrong_token {

            @ParameterizedTest
            @ValueSource(strings = {
                    "a", "a.a", "a.a.a", "bbb.b.bb", "23.232.33",
                    "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjQ0fQ...eAG4-BS7VDgku9pNxKwyQHqom8EOd3qmBeDOudK2g0s",
                    "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjQ0fQ.eAG4-BS7VDgku9pNxKwyQHqom8EOd3qmBeDOudK2g0s11111",
                    "eyJhbGciOJ9.eyJ1ciOjQ0fQ.eAG4OudK2g0s"
            })
            @DisplayName("false를 반환한다")
            void it_returns_false(String token) {
                assertThat(jwtUtil.validateToken(token)).isFalse();
            }
        }
    }
}
