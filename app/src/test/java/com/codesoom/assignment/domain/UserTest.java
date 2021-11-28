package com.codesoom.assignment.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {
    private static final String TEST_STRING = "TEST";

    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setup() {
        passwordEncoder = new BCryptPasswordEncoder();
    }

    @Test
    void changeWith() {
        User user = User.builder().build();

        user.changeWith(User.builder()
                .name(TEST_STRING)
                .build());

        assertThat(user.getName()).isEqualTo(TEST_STRING);
        assertThat(user.getPassword()).isEmpty();
    }

    @Test
    void changePassword() {
        User user = User.builder().name(TEST_STRING).build();

        user.changePassword(TEST_STRING, passwordEncoder);

        assertThat(user.getName()).isEqualTo(TEST_STRING);
        assertThat(user.getPassword()).isNotEqualTo(TEST_STRING);
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
        String password = passwordEncoder.encode(TEST_STRING);
        User user = User.builder()
                .password(password)
                .build();

        assertThat(user.authenticate(TEST_STRING, passwordEncoder)).isTrue();
        assertThat(user.authenticate("xxx", passwordEncoder)).isFalse();
    }

    @Test
    void authenticateWithDeletedUser() {
        User user = User.builder()
                .password(TEST_STRING)
                .deleted(true)
                .build();

        assertThat(user.authenticate(TEST_STRING, passwordEncoder)).isFalse();
        assertThat(user.authenticate("xxx", passwordEncoder)).isFalse();
    }
}
