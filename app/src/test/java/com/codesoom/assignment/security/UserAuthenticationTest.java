package com.codesoom.assignment.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserAuthenticationTest {

    @DisplayName("인증 생성 생성 후 인증 값을 리턴합니다.")
    @Test
    void createAuthentication() {
        UserAuthentication userAuthentication = new UserAuthentication(1L);

        assertThat(userAuthentication.getPrincipal()).isEqualTo(1L);
        assertThat(userAuthentication.getAuthorities()).isNotEmpty();
        assertThat(userAuthentication.getCredentials()).isNull();
    }
}
