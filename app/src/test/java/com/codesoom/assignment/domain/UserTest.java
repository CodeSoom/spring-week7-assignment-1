package com.codesoom.assignment.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("User 클래스")
class UserTest {

    private static final String ORIGIN_PASSWORD = "1111";
    private static final String CHANGE_PASSWORD = "2222";

    PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Nested
    @DisplayName("changePassword 메소드는")
    class Describe_changePassword {

        final User user = User.builder()
                .password(ORIGIN_PASSWORD)
                .build();

        @Test
        @DisplayName("비밀번호를 암호화해서 변경한다.")
        void it_changeEncryptPassword() {

            user.changePassword(CHANGE_PASSWORD, passwordEncoder);

            boolean isPasswordMatch = passwordEncoder.matches(CHANGE_PASSWORD, user.getPassword());

            assertThat(isPasswordMatch).isTrue();
        }
    }

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

        user.inActivate();

        assertThat(user.isDeleted()).isTrue();
    }

    @Test
    void authenticate() {
        User user = User.builder().build();
        user.changePassword("test", passwordEncoder);

        assertThat(user.authenticate("test", passwordEncoder)).isTrue();
        assertThat(user.authenticate("xxx", passwordEncoder)).isFalse();
    }

    @Test
    void authenticateWithDeletedUser() {
        User user = User.builder()
                .deleted(true)
                .build();
        user.changePassword("test", passwordEncoder);

        assertThat(user.authenticate("test", passwordEncoder)).isFalse();
        assertThat(user.authenticate("xxx", passwordEncoder)).isFalse();
    }
}
