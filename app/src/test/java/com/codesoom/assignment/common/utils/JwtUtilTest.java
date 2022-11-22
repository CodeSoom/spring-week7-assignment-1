package com.codesoom.assignment.common.utils;

import com.codesoom.assignment.session.application.exception.InvalidTokenException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.codesoom.assignment.support.AuthHeaderFixture.INVALID_VALUE_TOKEN_1;
import static com.codesoom.assignment.support.AuthHeaderFixture.VALID_TOKEN_1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtUtilTest {
    private static final String SECRET = VALID_TOKEN_1.시크릿_키();

    private final JwtUtil jwtUtil = new JwtUtil(SECRET);

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class encode_메서드는 {

        @Test
        @DisplayName("토큰을 secretKey로 인코딩하여 리턴한다")
        void it_returns_token() {
            String token = jwtUtil.encode(VALID_TOKEN_1.아이디());

            assertThat(token).isEqualTo(VALID_TOKEN_1.토큰_값());
        }
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class decode_메서드는 {

        @Nested
        @DisplayName("유효한 토큰이 주어질 경우")
        class Context_with_valid_token {

            @Test
            @DisplayName("userId가 포함된 클레임을 리턴한다")
            void it_returns_claims() {
                Long userId = jwtUtil.decode(VALID_TOKEN_1.토큰_값())
                        .get("userId", Long.class);

                assertThat(userId).isEqualTo(VALID_TOKEN_1.아이디());
            }
        }

        @Nested
        @DisplayName("유효하지 않은 토큰이 주어질 경우")
        class Context_with_invalid_token {

            @Test
            @DisplayName("InvalidTokenException 예외를 던진다")
            void it_returns_exception() {
                assertThatThrownBy(() -> jwtUtil.decode(INVALID_VALUE_TOKEN_1.토큰_값()))
                        .isInstanceOf(InvalidTokenException.class);
            }
        }

        @Nested
        @DisplayName("비어있는 토큰이 주어질 경우")
        class Context_with_blank_token {

            @Test
            @DisplayName("InvalidTokenException 예외를 던진다")
            void it_returns_exception() {
                assertThatThrownBy(() -> jwtUtil.decode(null))
                        .isInstanceOf(InvalidTokenException.class);

                assertThatThrownBy(() -> jwtUtil.decode(""))
                        .isInstanceOf(InvalidTokenException.class);

                assertThatThrownBy(() -> jwtUtil.decode("   "))
                        .isInstanceOf(InvalidTokenException.class);
            }
        }
    }
}
