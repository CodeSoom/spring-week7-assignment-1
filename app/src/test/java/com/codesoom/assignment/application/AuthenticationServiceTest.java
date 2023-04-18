package com.codesoom.assignment.application;

import com.codesoom.assignment.domain.User;
import com.codesoom.assignment.domain.UserRepository;
import com.codesoom.assignment.dto.SessionResponseData;
import com.codesoom.assignment.errors.InvalidTokenException;
import com.codesoom.assignment.errors.LoginFailException;
import com.codesoom.assignment.security.UserAuthentication;
import com.codesoom.assignment.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    private String validToken;
    private String invalidToken;
    private String validRefreshToken;

    private AuthenticationService authenticationService;

    private UserRepository userRepository = mock(UserRepository.class);

    @BeforeEach
    void setUp() {

        JwtUtil jwtUtil = new JwtUtil(SECRET);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        User user = User.builder()
                .id(1L)
                .role("ROLE_USER")
                .build();
        user.changePassword("test",passwordEncoder);

        validToken = jwtUtil.createAccessToken(user);
        validRefreshToken = jwtUtil.createAccessToken(user);
        user.registRefreshToken(validRefreshToken);

        invalidToken = validToken+"INVALID";

        authenticationService = new AuthenticationService(
                userRepository, jwtUtil, passwordEncoder);

        given(userRepository.findByEmail("tester@example.com"))
                .willReturn(Optional.of(user));

        given(userRepository.findByRefreshTokenAndDeletedIsFalse(validRefreshToken))
                .willReturn(Optional.of(user));
    }

    @Test
    void loginWithRightEmailAndPassword() {
        SessionResponseData responseData = authenticationService.login(
                "tester@example.com", "test");

        String accessToken = responseData.getAccessToken();
        String refreshToken = responseData.getRefreshToken();

        assertThat(accessToken).contains(".");

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
        Claims claims = authenticationService.parseToken(validToken);
        Long userId = claims.get("userId",Long.class);

        assertThat(userId).isEqualTo(1L);
    }

    @Test
    void parseTokenWithInvalidToken() {
        assertThatThrownBy(
                () -> authenticationService.parseToken(invalidToken)
        ).isInstanceOf(InvalidTokenException.class);
    }

    @Test
    void reissueAccessTokenWithValidRefreshToken() {
        SessionResponseData responseData = authenticationService.reissueAccessToken(validToken);
        assertThat(responseData.getAccessToken()).contains(".");
        assertThat(responseData.getRefreshToken()).isEqualTo(validToken);
    }
}
