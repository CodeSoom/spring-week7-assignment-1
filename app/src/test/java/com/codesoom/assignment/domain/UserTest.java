package com.codesoom.assignment.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserTest {

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Test
    void changeWith() {
        User user = User.builder().build();

        user.changeWith(User.builder()
                .name("TEST")
                .password("TEST")
                .build());

        assertThat(user.getName()).isEqualTo("TEST");
        assertThat(user.getPassword()).isEqualTo("TEST");
    }

    @Test
    void destroy() {
        User user = User.builder().build();

        assertThat(user.isDeleted()).isFalse();

        user.destroy();

        assertThat(user.isDeleted()).isTrue();
    }

    @Nested
    @DisplayName("authenticate 메소드는")
    class Describe_authenticate {

        @Nested
        @DisplayName("deleted user가 아니고 패스워드가 일치하면")
        class Context_when_user_is_not_deleted_and_password_matches {
            User user;

            @BeforeEach
            void setUp() {
                user = User.builder()
                        .password("test")
                        .build();

                user.encodePassword(passwordEncoder);
            }

            @Test
            @DisplayName("true를 반환한다.")
            void it_returns_true() {
                boolean actual = user.authenticate("test", passwordEncoder);

                assertThat(actual).isTrue();
            }
        }

        @Nested
        @DisplayName("유저가 deleted 상태이면")
        class Context_when_deleted_user {
            User user;

            @BeforeEach
            void setUp() {
                user = User.builder()
                        .password("test")
                        .deleted(true)
                        .build();

                user.encodePassword(passwordEncoder);
            }

            @Test
            @DisplayName("false 반환한다.")
            void it_returns_true() {
                boolean actual = user.authenticate("test", passwordEncoder);

                assertThat(actual).isFalse();
            }
        }

        @Nested
        @DisplayName("일치하지 않은 패스워드가 주어지면")
        class Context_with_mismatched_password {
            User user;

            @BeforeEach
            void setUp() {
                user = User.builder()
                        .password("test")
                        .build();

                user.encodePassword(passwordEncoder);
            }

            @Test
            @DisplayName("false 반환한다.")
            void it_returns_true() {
                boolean actual = user.authenticate("test1", passwordEncoder);

                assertThat(actual).isFalse();
            }
        }
    }

    @Nested
    @DisplayName("encodePassword 메소드는")
    class Describe_encodePassword {

        @Nested
        @DisplayName("패스워드가 널이 아니면")
        class Context_when_password_is_nonNull {
            final User user = User.builder()
                    .password("test")
                    .build();

            @Test
            @DisplayName("처음 생성된 패스워드랑 다르다.")
            void it_is_different_from_first() {
                user.encodePassword(passwordEncoder);

                assertThat(user.getPassword()).isNotEqualTo("test");
            }
        }

        @Nested
        @DisplayName("패스워드가 널이면")
        class Context_when_password_is_null {
            final User user = User.builder()
                    .build();

            @Test
            @DisplayName("IllegalStateException 예외를 던진다.")
            void it_throws_IllegalStateException() {
                assertThatThrownBy(() -> user.encodePassword(passwordEncoder))
                        .isInstanceOf(IllegalStateException.class);
            }
        }
    }
}
