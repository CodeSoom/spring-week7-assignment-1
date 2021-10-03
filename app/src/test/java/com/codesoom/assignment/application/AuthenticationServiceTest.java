package com.codesoom.assignment.application;

import com.codesoom.assignment.domain.User;
import com.codesoom.assignment.domain.UserRepository;
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
    private static final String SECRET = "12345678901234567890123456789012";

    private static final String VALID_TOKEN = "eyJhbGciOiJIUzI1NiJ9." +
            "eyJ1c2VySWQiOjF9.ZZ3CUl0jxeLGvQ1Js5nG2Ty5qGTlqai5ubDMXZOdaDk";
    private static final String INVALID_TOKEN = "eyJhbGciOiJIUzI1NiJ9." +
            "eyJ1c2VySWQiOjF9.ZZ3CUl0jxeLGvQ1Js5nG2Ty5qGTlqai5ubDMXZOdaD0";

    private AuthenticationService authenticationService;

    private UserRepository userRepository = mock(UserRepository.class);

    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        JwtUtil jwtUtil = new JwtUtil(SECRET);
        passwordEncoder = new BCryptPasswordEncoder();

        authenticationService = new AuthenticationService(
                userRepository, jwtUtil, passwordEncoder);

        User user = User.builder()
                .name("name")
                .email("tester@example.com")
                .build();

        user.changePassword("test", passwordEncoder);

        given(userRepository.findByEmail("tester@example.com"))
                .willReturn(Optional.of(user));
    }

    @Nested
    @DisplayName("로그인")
    class Login {
        @Nested
        @DisplayName("유효한 이메일과 비밀번호로 요청하면")
        class WithRightEmailAndPassword {
            @Test
            @DisplayName("토큰을 반환한다.")
            void returnAccessToken() {
                String accessToken = authenticationService.login(
                        "tester@example.com", "test");

                assertThat(accessToken).isEqualTo(VALID_TOKEN);

                verify(userRepository).findByEmail("tester@example.com");
            }
        }

        @Nested
        @DisplayName("잘못된 이메일로 요청하면")
        class WithWrongEmail {
            @Test
            @DisplayName("로그인에 실패했다는 내용의 예외를 던진다.")
            void throwLoginFailException() {
                assertThatThrownBy(
                        () -> authenticationService.login("badguy@example.com", "test")
                ).isInstanceOf(LoginFailException.class);

                verify(userRepository).findByEmail("badguy@example.com");
            }
        }

        @Nested
        @DisplayName("잘못된 비밀번호로 요청하면")
        class WithWrongPassword {
            @Test
            @DisplayName("로그인에 실패했다는 내용의 예외를 던진다.")
            void throwError() {
                assertThatThrownBy(
                        () -> authenticationService.login("tester@example.com", "xxx")
                ).isInstanceOf(LoginFailException.class);

                verify(userRepository).findByEmail("tester@example.com");
            }
        }
    }

    @Nested
    @DisplayName("토큰 확인")
    class ParseToken {
        @Nested
        @DisplayName("유효한 토큰으로 요청하면")
        class WithValidToken {
            @Test
            @DisplayName("회원 식별번호를 반환한다.")
            void parseTokenWithValidToken() {
                Long userId = authenticationService.parseToken(VALID_TOKEN);

                assertThat(userId).isEqualTo(1L);
            }
        }

        @Nested
        @DisplayName("유효하지 않은 토큰으로 요청하면")
        class WithInvalidToken {
            @Test
            @DisplayName("유효하지 않은 토큰이라는 내용의 예외를 던진다.")
            void parseTokenWithInvalidToken() {
                assertThatThrownBy(
                        () -> authenticationService.parseToken(INVALID_TOKEN)
                ).isInstanceOf(InvalidTokenException.class);
            }
        }
    }
}
