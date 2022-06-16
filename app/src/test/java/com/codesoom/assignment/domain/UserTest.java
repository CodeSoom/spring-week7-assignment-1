package com.codesoom.assignment.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * User Entity에 대한 테스트 클래스
 */
class UserTest {
    private static final Long ID = 1L;
    private static final String NAME = "TestUser";
    private static final String EMAIL = "test@example.com";
    private static final String PASSWORD = "TEST";

    private User user;
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        user = User.builder().build();
        passwordEncoder = new BCryptPasswordEncoder();
    }

    // TODO: 객체 생성 테스트
    //  - Builder로 User를 생성할 수 있어야 한다.
    @Nested
    @DisplayName("User는")
    class Describe_user {
        @Nested
        @DisplayName("빌더를 통해")
        class Context_by_builder {
            User subject() {
                return user = User.builder()
                        .id(ID)
                        .name(NAME)
                        .email(EMAIL)
                        .password(PASSWORD)
                        .build();
            }

            @Test
            @DisplayName("객체를 생성한다")
            void It_creates_user() {
                User user = subject();

                assertThat(user.getId()).isEqualTo(ID);
                assertThat(user.getName()).isEqualTo(NAME);
                assertThat(user.getEmail()).isEqualTo(EMAIL);
                assertThat(user.getPassword()).isEqualTo(PASSWORD);
            }
        }
    }

    // TODO: 암호화 테스트
    //  - 비밀번호를 암호화할 수 있어야 한다.
    @Nested
    @DisplayName("encodePassword 메서드는")
    class Describe_encodePassword_method {
        @Nested
        @DisplayName("사용자가 입력한 비밀번호가 주어지면")
        class Context_if_raw_password_given {
            @BeforeEach
            void setUp() {
                user.encodePassword(PASSWORD, passwordEncoder);
            }

            @Test
            @DisplayName("그 비밀번호를 암호화한다")
            void It_encodes_raw_password() {
                assertThat(user.getPassword()).isNotEqualTo(PASSWORD);
            }
        }
    }

    // TODO: 인증 테스트
    //  - 주어진 비밀번호와 암호화된 비밀번호가 일치하는지 인증할 수 있어야 한다.

    // TODO: 데이터 수정 테스트
    //  - name, password를 수정할 수 있어야 한다.
    //  - 이 때 password는 암호화되어야 한다.
}
