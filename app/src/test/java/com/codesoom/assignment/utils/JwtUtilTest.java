package com.codesoom.assignment.utils;

import com.codesoom.assignment.errors.InvalidTokenException;
import com.codesoom.assignment.errors.NotSupportedUserIdException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtUtilTest {
    private static final String SECRET = "12345678901234567890123456789012";

    private static final String INVALID_TOKEN = "eyJhbGciOiJIUzI1NiJ9." +
            "eyJ1c2VySWQiOjF9.ZZ3CUl0jxeLGvQ1Js5nG2Ty5qGTlqai5ubDMXZOdaD0";

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil(SECRET);
    }

    @ParameterizedTest
    @ValueSource(longs = {1, 3, 6, 15, 1111, 99999, Long.MAX_VALUE})
    void encodeAndDecode(Long id) {
        final String token = jwtUtil.encode(id);
        final Long decodedId = jwtUtil.decode(token);

        assertThat(decodedId).isEqualTo(id);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {Long.MIN_VALUE, -1, -30, -123901238, -87827273, -99999})
    void encodeWithInvalidData(Long id) {
        assertThatThrownBy(() -> jwtUtil.encode(id))
                .isInstanceOf(NotSupportedUserIdException.class);
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
