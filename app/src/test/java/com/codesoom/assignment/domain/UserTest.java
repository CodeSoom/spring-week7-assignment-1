package com.codesoom.assignment.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {
    private PasswordEncoder passwordEncoder;

    private final static String TEST_NAME = "TEST";
    private final static String PASSWORD = "TEST";
    private final static String WRONG_PASSWORD = "XXX";

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
    }

    @Test
    void changeWith() {
        User user = User.builder().build();

        user.changeWith(User.builder()
                .name(TEST_NAME)
                .password(PASSWORD)
                .build());

        assertThat(user.getName()).isEqualTo(TEST_NAME);
        assertThat(user.getPassword()).isEqualTo(PASSWORD);
    }

    @Test
    void changePassword() {
        User user = User.builder().build();

        user.changePassword(PASSWORD, passwordEncoder);

        assertThat(user.getPassword()).isNotEmpty();
        assertThat(user.getPassword()).isNotEqualTo(PASSWORD);
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
        User user = User.builder().build();
        user.changePassword(PASSWORD, passwordEncoder);

        assertThat(user.authenticate(PASSWORD, passwordEncoder)).isTrue();
        assertThat(user.authenticate(WRONG_PASSWORD, passwordEncoder)).isFalse();
    }

    @Test
    void authenticateWithDeletedUser() {
        User user = User.builder().deleted(true).build();
        user.changePassword(PASSWORD, passwordEncoder);

        assertThat(user.authenticate(PASSWORD, passwordEncoder)).isFalse();
        assertThat(user.authenticate(WRONG_PASSWORD, passwordEncoder)).isFalse();
    }
}
