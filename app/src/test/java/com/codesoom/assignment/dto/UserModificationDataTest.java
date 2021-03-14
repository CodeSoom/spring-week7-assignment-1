package com.codesoom.assignment.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserModificationDataTest {
    @Test
    @DisplayName("getPassword 메서드 테스트")
    void getPasswordTest() {
        assertThat(UserModificationData.builder().password("password").build().getPassword())
                .isEqualTo("password");
    }

    @Test
    @DisplayName("toString 메서드 테스트")
    void toStringTest() {
        assertThat(UserModificationData.builder().password("password").toString())
                .contains("password");
    }
}
