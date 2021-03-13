package com.codesoom.assignment.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import static org.assertj.core.api.Assertions.assertThat;

class UserAuthenticationTest {

    @DisplayName("UserAuthentication 생성자는 유저 인증 객체를 생성합니다.")
    @Test
    void create() {
        Long userId = 1L;
        String role = "ROLE_USER";

        UserAuthentication userAuthentication = new UserAuthentication(userId, role);

        assertThat(userAuthentication.getCredentials()).isNull();
        assertThat(userAuthentication.getPrincipal()).isEqualTo(userId);
        assertThat(userAuthentication.isAuthenticated()).isTrue();
        assertThat(userAuthentication.getAuthorities()).contains(new SimpleGrantedAuthority(role));
    }

}
