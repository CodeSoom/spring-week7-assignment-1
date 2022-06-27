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
    private static final String NEW_NAME = "NewTester";
    private static final String NEW_EMAIL = "newtester@example.com";
    private static final String NEW_PASSWORD = "NEWTEST";

    private User user;
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        user = User.builder().build();
        passwordEncoder = new BCryptPasswordEncoder();
    }

    @Nested
    @DisplayName("User는")
    class Describe_user {
        @Nested
        @DisplayName("빌더로 객체를 생성한다")
        class It_creates_user_by_builder {
            User subject() {
                return user = User.builder()
                        .id(ID)
                        .name(NAME)
                        .email(EMAIL)
                        .password(PASSWORD)
                        .build();
            }

            @Test
            void test() {
                User user = subject();

                assertThat(user.getId()).isEqualTo(ID);
                assertThat(user.getName()).isEqualTo(NAME);
                assertThat(user.getEmail()).isEqualTo(EMAIL);
                assertThat(user.getPassword()).isEqualTo(PASSWORD);
            }
        }
    }

    @Nested
    @DisplayName("encodePassword 메서드는")
    class Describe_encodePassword_method {
        @Nested
        @DisplayName("비밀번호를 암호화한다")
        class It_encodes_password {
            @BeforeEach
            void setUp() {
                user.encodePassword(PASSWORD, passwordEncoder);
            }

            @Test
            void test() {
                assertThat(user.getPassword()).isNotEqualTo(PASSWORD);
            }
        }
    }

    @Nested
    @DisplayName("authenticate 메서드는")
    class Describe_authenticate_method {
        @Nested
        @DisplayName("비밀번호를 인증한다")
        class It_authenticates_password {
            @BeforeEach
            void setUp() {
                user.encodePassword(PASSWORD, passwordEncoder);
            }

            @Test
            void test() {
                assertThat(user.authenticatePassword(PASSWORD, passwordEncoder)).isTrue();
            }
        }
    }

    @Nested
    @DisplayName("update 메서드는")
    class Describe_update_method {
        @BeforeEach
        void setUp() {
            user = User.builder()
                    .id(ID)
                    .name(NAME)
                    .email(EMAIL)
                    .password(PASSWORD)
                    .build();
        }

        @Nested
        @DisplayName("User를 수정한다")
        class It_updates_User {
            User subject() {
                user.update(NEW_NAME, NEW_EMAIL, NEW_PASSWORD);
                user.encodePassword(NEW_PASSWORD, passwordEncoder);

                return user;
            }

            @Test
            void test() {
                User updatedUser = subject();

                assertThat(updatedUser.getId()).isEqualTo(ID);
                assertThat(updatedUser.getName()).isEqualTo(NEW_NAME);
                assertThat(updatedUser.getEmail()).isEqualTo(NEW_EMAIL);
                assertThat(user.authenticatePassword(NEW_PASSWORD, passwordEncoder)).isTrue();
            }
        }
    }
}
