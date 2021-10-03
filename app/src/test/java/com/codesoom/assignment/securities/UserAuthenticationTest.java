package com.codesoom.assignment.securities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserAuthenticationTest {
    private UserAuthentication userAuthentication;

    @BeforeEach
    void setup() {
        userAuthentication = new UserAuthentication(Roles.USER, 1L,"aaa.bbb.ccc");
    }

    @Nested
    @DisplayName("getPrincipal")
    class GetPrincipal {
        @Test
        @DisplayName("회원 식별자를 반환한다.")
        void getPrincipal() {
            assertThat(userAuthentication.getPrincipal()).isEqualTo(1L);
        }
    }

    @Nested
    @DisplayName("getCredentials")
    class GetCredentials {
        @Test
        @DisplayName("회원 인증 키를 리턴한다.")
        void getPrincipal() {
            assertThat(userAuthentication.getCredentials()).isEqualTo("aaa.bbb.ccc");
        }
    }
}
