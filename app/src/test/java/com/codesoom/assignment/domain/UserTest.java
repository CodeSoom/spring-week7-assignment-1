package com.codesoom.assignment.domain;

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

    @Test
    void authenticate() {
        User user = User.builder()
                .password("test")
                .build();

        assertThat(user.authenticate("test")).isTrue();
        assertThat(user.authenticate("xxx")).isFalse();
    }

    @Test
    void authenticateWithDeletedUser() {
        User user = User.builder()
                .password("test")
                .deleted(true)
                .build();

        assertThat(user.authenticate("test")).isFalse();
        assertThat(user.authenticate("xxx")).isFalse();
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
