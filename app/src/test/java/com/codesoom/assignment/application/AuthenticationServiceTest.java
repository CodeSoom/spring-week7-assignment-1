package com.codesoom.assignment.application;

import com.codesoom.assignment.domain.User;
import com.codesoom.assignment.domain.UserRepository;
import com.codesoom.assignment.errors.InvalidTokenException;
import com.codesoom.assignment.errors.LoginFailException;
import com.codesoom.assignment.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class AuthenticationServiceTest {
    private static final String SECRET = "12345678901234567890123456789012";

    private static final String VALID_TOKEN = "eyJhbGciOiJIUzI1NiJ9." +
            "eyJ1c2VySWQiOjEsInJvbGUiOiJST0xFX1VTRVIifQ." +
            "SgLtYfTVUdvPF-gIP006U-_B7-wWMSUcJD3eoSOxHsE";
    private static final String INVALID_TOKEN = VALID_TOKEN + "WRONG";

    private AuthenticationService authenticationService;

    private UserRepository userRepository = mock(UserRepository.class);

    @BeforeEach
    void setUp() {
        JwtUtil jwtUtil = new JwtUtil(SECRET);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        authenticationService = new AuthenticationService(
                userRepository, jwtUtil, passwordEncoder);

        User user = User.builder()
                .id(1L)
                .build();
        user.changePassword("test", passwordEncoder);

        given(userRepository.findByEmail("tester@example.com"))
                .willReturn(Optional.of(user));
    }

    @Test
    void loginWithRightEmailAndPassword() {
        String accessToken = authenticationService.login(
                "tester@example.com", "test");

        assertThat(accessToken).isEqualTo(VALID_TOKEN);

        verify(userRepository).findByEmail("tester@example.com");
    }

    @Test
    void loginWithWrongEmail() {
        assertThatThrownBy(
                () -> authenticationService.login("badguy@example.com", "test")
        ).isInstanceOf(LoginFailException.class);

        verify(userRepository).findByEmail("badguy@example.com");
    }

    @Test
    void loginWithWrongPassword() {
        assertThatThrownBy(
                () -> authenticationService.login("tester@example.com", "xxx")
        ).isInstanceOf(LoginFailException.class);

        verify(userRepository).findByEmail("tester@example.com");
    }

    @Test
    void parseTokenWithValidToken() {
        Claims claims = authenticationService.parseToken(VALID_TOKEN);

        assertThat(claims.get("userId", Long.class)).isEqualTo(1L);
        assertThat(claims.get("role")).isEqualTo("ROLE_USER");
    }

    @Test
    void parseTokenWithInvalidToken() {
        assertThatThrownBy(
                () -> authenticationService.parseToken(INVALID_TOKEN)
        ).isInstanceOf(InvalidTokenException.class);
    }
}
