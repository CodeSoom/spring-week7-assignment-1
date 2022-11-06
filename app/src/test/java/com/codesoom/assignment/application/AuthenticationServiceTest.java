package com.codesoom.assignment.application;

import com.codesoom.assignment.domain.User;
import com.codesoom.assignment.errors.InvalidTokenException;
import com.codesoom.assignment.errors.LoginFailException;
import com.codesoom.assignment.infra.JpaUserRepository;
import com.codesoom.assignment.utils.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class AuthenticationServiceTest {

    private static final String SECRET = "12345678901234567890123456789012";
    private static final String EMAIL = "tester@example.com";
    private static final String PASSWORD = "test1234";

    @Autowired
    private JpaUserRepository userRepository;

    private JwtUtil jwtUtil = new JwtUtil(SECRET);
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        authenticationService = new AuthenticationService(userRepository, jwtUtil, passwordEncoder);
    }

    @Nested
    @DisplayName("login 메소드는")
    class Describe_login {

        @Nested
        @DisplayName("존재하는 회원 정보가 주어지면")
        class Context_with_valid_user_info {
            private Long userId;

            @BeforeEach
            void setUp() {
                User user = userRepository.save(User.builder()
                        .email(EMAIL)
                        .password(passwordEncoder.encode(PASSWORD))
                        .build());

                userId = user.getId();
            }

            @Test
            @DisplayName("토큰을 반환한다")
            void it_returns_token() {
                String accessToken = authenticationService.login(EMAIL, PASSWORD);
                assertThat(accessToken).isEqualTo(jwtUtil.encode(userId));
            }
        }

        @Nested
        @DisplayName("잘못된 회원 정보가 주어지면")
        class Context_with_wrong_email_or_password {
            private final String wrongEmail = "wrong" + EMAIL;
            private final String wrongPassword = "wrong" + PASSWORD;

            @BeforeEach
            void setUp() {
                userRepository.save(User.builder()
                        .email(EMAIL)
                        .password(passwordEncoder.encode(PASSWORD))
                        .build());
            }

            @Test
            @DisplayName("예외를 던진다")
            void it_throws_exception() {
                assertThatThrownBy(() -> authenticationService.login(wrongEmail, PASSWORD))
                        .isInstanceOf(LoginFailException.class);

                assertThatThrownBy(() -> authenticationService.login(EMAIL, wrongPassword))
                        .isInstanceOf(LoginFailException.class);
            }
        }
    }

    @Nested
    @DisplayName("parseToken 메소드는")
    class Desribe_parseToken {

        @Nested
        @DisplayName("유효한 토큰이 주어지면")
        class Context_with_valid_token {
            private String token;
            private Long userId;

            @BeforeEach
            void setUp() {
                User user = userRepository.save(User.builder()
                        .email(EMAIL)
                        .password(passwordEncoder.encode(PASSWORD))
                        .build());

                userId = user.getId();
                token = jwtUtil.encode(userId);
            }

            @Test
            @DisplayName("회원 정보를 반환한다")
            void it_returns_user_id() {
                Long parsedResult = authenticationService.parseToken(token);

                assertThat(parsedResult).isEqualTo(userId);
            }
        }

        @Nested
        @DisplayName("빈 토큰이 주어지면")
        class Context_with_empty_token {

            @ParameterizedTest
            @NullAndEmptySource
            @DisplayName("예외를 던진다")
            void it_throws_exception(String token) {
                assertThatThrownBy(() -> authenticationService.parseToken(token))
                        .isInstanceOf(InvalidTokenException.class);
            }
        }

        @Nested
        @DisplayName("클레임 값이 잘못된 토큰이 주어지면")
        class Context_with_wrong_token {
            private final String wrongToken
                    = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOiJ1c2VySWQifQ.8XY0MfjPyLbWkOmQpGpmyo_KccFIgfsfhgEh3fIIuyQ";

            @Test
            @DisplayName("예외를 던진다")
            void it_throws_exception() {
                assertThatThrownBy(() -> authenticationService.parseToken(wrongToken))
                        .isInstanceOf(InvalidTokenException.class);
            }
        }
    }
}
