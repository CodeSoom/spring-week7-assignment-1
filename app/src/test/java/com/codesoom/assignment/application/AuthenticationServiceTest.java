package com.codesoom.assignment.application;


import com.codesoom.assignment.application.dto.SessionCommand;
import com.codesoom.assignment.application.dto.UserCommand;
import com.codesoom.assignment.domain.User;
import com.codesoom.assignment.errors.LoginFailException;
import com.codesoom.assignment.mapper.UserFactory;
import com.codesoom.assignment.security.JwtTokenProvider;
import com.codesoom.assignment.utils.UserSampleFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@DisplayName("AuthenticationService 클래스")
class AuthenticationServiceTest {

    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserFactory userFactory;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Nested
    @DisplayName("login 메소드는")
    class Describe_login {
        @Nested
        @DisplayName("유효한 로그인 정보가 주어지면")
        class Context_with_valid_login_info {
            private final UserCommand.Register register = userFactory.of(UserSampleFactory.createRequestParam());
            private final User savedUser = userService.registerUser(register);
            private final String givenToken = jwtTokenProvider.createToken(savedUser.getId());

            @Test
            @DisplayName("액세스 토큰을 생성하고 리턴한다.")
            void it_returns_access_token() {
                SessionCommand.SessionRequest command = SessionCommand.SessionRequest.builder()
                        .email(register.getEmail())
                        .password(register.getPassword())
                        .build();

                String actualToken = authenticationService.login(command);

                assertThat(actualToken).isEqualTo(givenToken);
            }
        }

        @Nested
        @DisplayName("유효하지않은 이메일이 주어지면")
        class Context_with_invalid_email {
            @Test
            @DisplayName("예외를 던진다.")
            void it_throws_exception() {
                SessionCommand.SessionRequest command = SessionCommand.SessionRequest.builder()
                        .email("invalid@email.com")
                        .password("test1234")
                        .build();

                assertThatThrownBy(() -> authenticationService.login(command)).isInstanceOf(LoginFailException.class);
            }
        }

        @Nested
        @DisplayName("잘못된 비밀번호가 주어지면")
        class Context_with_wrong_password {
            private final UserCommand.Register register = userFactory.of(UserSampleFactory.createRequestParam());

            @BeforeEach
            void prepare() {
                final User savedUser = userService.registerUser(register);
            }

            @Test
            @DisplayName("예외를 던진다.")
            void it_throws_exception() {
                SessionCommand.SessionRequest command = SessionCommand.SessionRequest.builder()
                        .email(register.getEmail())
                        .password("invalid_password")
                        .build();

                assertThatThrownBy(() -> authenticationService.login(command)).isInstanceOf(LoginFailException.class);
            }
        }
    }
}
