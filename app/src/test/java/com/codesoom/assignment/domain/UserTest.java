package com.codesoom.assignment.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
    }

    @Test
    void changeWith() {
        User user = User.builder().build();

        user.changeWith(User.builder()
                .name("TEST")
                .build());
//        user.changePassword("TEST");

        assertThat(user.getName()).isEqualTo("TEST");
        assertThat(user.getPassword()).isEqualTo("");
    }

    @Test
    void changePassword() {
        String password = "TEST";
        User user = User.builder().build();
        user.changePassword(password , passwordEncoder);

        assertThat(user.getPassword()).isNotEmpty();
        assertThat(user.getPassword()).isNotEqualTo(password);
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
        user.changePassword("TEST" , passwordEncoder);

        assertThat(user.authenticate("TEST" , passwordEncoder)).isTrue();
        assertThat(user.authenticate("xxx" , passwordEncoder)).isFalse();
    }

    @Test
    @DisplayName("authenticate() 삭제된 사용자라면 false를 반환한다.")
    void authenticateWithDeletedUser() {
        User user = User.builder()
                .password("test")
                .deleted(true)
                .build();

        assertThat(user.authenticate("test" , passwordEncoder)).isFalse();
    }
}
