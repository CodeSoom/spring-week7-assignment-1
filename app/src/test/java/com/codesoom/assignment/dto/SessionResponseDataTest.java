package com.codesoom.assignment.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SessionResponseDataTest {
    @Test
    @DisplayName("toString 메서드 테스트")
    void toStringTest() {
        assertThat(SessionResponseData.builder().accessToken("token").toString())
                .contains("token");
    }
}
