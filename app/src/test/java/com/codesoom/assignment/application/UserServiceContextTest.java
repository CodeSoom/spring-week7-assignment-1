package com.codesoom.assignment.application;

import com.codesoom.assignment.annotations.Utf8MockMvc;
import com.codesoom.assignment.domain.User;
import com.codesoom.assignment.domain.UserRepository;
import com.codesoom.assignment.dto.UserRegistrationData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Utf8MockMvc
@DisplayName("UserService 의")
class UserServiceContextTest {
    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Nested
    @DisplayName("registerUser() 메서드는")
    class Describe_register_user {

        @Nested
        @DisplayName("유효한 유저 정보를 받았을 때")
        class Context_with_valid_registration_input {
            UserRegistrationData userRegistrationData;

            Context_with_valid_registration_input() {
                userRegistrationData = UserRegistrationData
                        .builder()
                        .email("testuser@example.com")
                        .name("김테스트")
                        .password("testtest123")
                        .build();
            }

            User subject() {
                return userService.registerUser(userRegistrationData);
            }

            @Test
            @DisplayName("유저를 정상적으로 등록한다.")
            void it_registers_user() {
                User user = subject();

                // 모든 정보가 제대로 입력되었는지 확인
                assertThat(user.getId()).isNotNull();
                assertThat(user.getEmail()).isNotNull();
                assertThat(user.getName()).isNotNull();
                assertThat(user.getPassword()).isNotNull();
            }

            @Test
            @DisplayName("저장된 User 의 비밀번호는 암호화된다.")
            void it_encrypts_password() {
                User user = subject();

                // 비밀번호가 가입할 때 입력했던 평문과 다른지 확인
                assertThat(user.getPassword())
                        .isNotEqualTo(userRegistrationData.getPassword());

                // 비밀번호가 정상적으로 인코딩 되었는지 확인
                passwordEncoder.matches(userRegistrationData.getPassword()
                        , user.getPassword());
            }
        }
    }
}