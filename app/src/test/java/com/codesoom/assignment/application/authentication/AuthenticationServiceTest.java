package com.codesoom.assignment.application.authentication;

import com.codesoom.assignment.UserNotFoundException;
import com.codesoom.assignment.domain.TestUserRepositoryDouble;
import com.codesoom.assignment.domain.User;
import com.codesoom.assignment.domain.UserRepository;
import com.codesoom.assignment.utils.JwtUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("AuthenticationService 에서")
class AuthenticationServiceTest {
    private final static String USERNAME = "username1";
    private final static String EMAIL = "auth@auth.com";
    private final static String PASSWORD = "password";

    @Autowired
    private TestUserRepositoryDouble userRepository;
    @Autowired
    private AuthenticationService authenticationService;

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    /**
     * 하나의 User를 생성해 등록합니다
     * @return 생성한 User
     */
    private User createUser() {
        User user = User.of(
                USERNAME,
                EMAIL,
                PASSWORD
        );
        return userRepository.save(user);
    }


    @Nested
    @DisplayName("login 메소드는")
    class Describe_of_login {

        @Nested
        @DisplayName("로그인할 수 있는 사용자의 이메일과 패스워드가 주어지면")
        class Context_with_valid_email_and_password {
            private String loginEmail;
            private String password;

            @BeforeEach
            void setUp() {
                User user = createUser();
                loginEmail = user.getEmail();
                password = user.getPassword();
            }

            @Test
            @DisplayName("이메일에 맞는 사용자를 찾아서 사용자의 id로 claim을 등록한 jwt 토큰을 리턴한다")
            void it_return_jwt_token() {
                String jwtToken = authenticationService.login(loginEmail, password);

                assertThat(jwtToken).isNotEmpty();
            }
        }

        @Nested
        @DisplayName("로그인할 수 없는 사용자의 이메일이 주어지면")
        class Context_with_invalid_email {
            private String loginEmail;
            private String password;

            @BeforeEach
            void setUp() {
                User user = createUser();
                loginEmail = user.getEmail();
                password = user.getPassword();

                userRepository.delete(user);
            }

            @Test
            @DisplayName("UserNotFoundException을 던진다")
            void it_throw_userNotFoundException() {
                assertThatThrownBy(() -> authenticationService.login(loginEmail, password))
                        .isInstanceOf(UserNotFoundException.class);
            }

        }
    }
}