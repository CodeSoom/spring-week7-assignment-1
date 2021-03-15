package com.codesoom.assignment.application;

import com.codesoom.assignment.domain.User;
import com.codesoom.assignment.domain.UserRepository;
import com.codesoom.assignment.dto.SessionRequestData;
import com.codesoom.assignment.errors.InvalidTokenException;
import com.codesoom.assignment.errors.LoginFailException;
import com.codesoom.assignment.utils.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
    private AuthenticationService authenticationService;

    private final UserRepository userRepository = mock(UserRepository.class);

    private static final String SECRET = "12345678901234567890123456789012";
    private static final String VALID_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjF9.ZZ3CUl0jxeLGvQ1Js5nG2Ty5qGTlqai5ubDMXZOdaDk";
    private static final String INVALID_TOKEN = VALID_TOKEN + "WRONG";

    private static final Long ID = 1L;
    private static final String EMAIL = "yhyojoo@codesoom.com";
    private static final String PASSWORD = "112233!!";

    private static final String WRONG_EMAIL = "hyo@codesoom.com";
    private static final String WRONG_PASSWORD = "123!";

    @BeforeEach
    void setUp() {
        JwtUtil jwtUtil = new JwtUtil(SECRET);

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        authenticationService = new AuthenticationService(userRepository, jwtUtil, passwordEncoder);

        User user = User.builder()
                .id(ID)
                .email(EMAIL)
                .build();

        user.changePassword(PASSWORD);
        
        given(userRepository.findByEmail(EMAIL)).willReturn(Optional.of(user));
    }

    @Nested
    @DisplayName("login 메소드는")
    class Describe_login {
        String accessToken;
        SessionRequestData givenValidLoginData;
        SessionRequestData givenWrongEmail;
        SessionRequestData givenWrongPassword;

        @Nested
        @DisplayName("정상적인 로그인 정보가 주어진다면")
        class Context_with_right_email_and_password {

            @BeforeEach
            void setUp() {
                givenValidLoginData = SessionRequestData.builder()
                        .email(EMAIL)
                        .password(PASSWORD)
                        .build();

                accessToken = authenticationService.login(givenValidLoginData);
            }

            @Test
            @DisplayName("유효한 토큰을 발급한다")
            void it_returns_token() {
                assertThat(accessToken).contains(VALID_TOKEN);
                verify(userRepository).findByEmail(EMAIL);
            }
        }

        @Nested
        @DisplayName("잘못된 이메일이 주어진다면")
        class Context_with_wrong_email {

            @BeforeEach
            void setUp() {
                givenWrongEmail = SessionRequestData.builder()
                        .email(WRONG_EMAIL)
                        .password(PASSWORD)
                        .build();
            }

            @Test
            @DisplayName("예외를 던진다")
            void it_throws_exception() {
                assertThatThrownBy(
                        () -> authenticationService.login(givenWrongEmail)
                ).isInstanceOf(LoginFailException.class);

                verify(userRepository).findByEmail(WRONG_EMAIL);
            }
        }

        @Nested
        @DisplayName("잘못된 비밀번호가 주어진다면")
        class Context_with_wrong_password {

            @BeforeEach
            void setUp() {
                givenWrongPassword = SessionRequestData.builder()
                        .email(EMAIL)
                        .password(WRONG_PASSWORD)
                        .build();
            }

            @Test
            @DisplayName("예외를 던진다")
            void it_throws_exception() {
                assertThatThrownBy(
                        () -> authenticationService.login(givenWrongPassword)
                ).isInstanceOf(LoginFailException.class);

                verify(userRepository).findByEmail(EMAIL);
            }
        }
    }

    @Test
    void parseTokenWithValidToken() {
        Long userId = authenticationService.parseToken(VALID_TOKEN);

        assertThat(userId).isEqualTo(1L);
    }

    @Test
    void parseTokenWithInvalidToken() {
        assertThatThrownBy(
                () -> authenticationService.parseToken(INVALID_TOKEN)
        ).isInstanceOf(InvalidTokenException.class);
    }

    @Test
    void parseTokenWithEmptyToken() {
        assertThatThrownBy(
                () -> authenticationService.parseToken(null)
        ).isInstanceOf(InvalidTokenException.class);

        assertThatThrownBy(
                () -> authenticationService.parseToken("")
        ).isInstanceOf(InvalidTokenException.class);
    }
}
