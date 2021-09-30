package com.codesoom.assignment.user.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    @Test
    void authenticate() {
        User user = User.builder()
                .password("test")
                .build();

        assertThat(user.authenticate("test")).isTrue();
    }
}