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
        userAuthentication = new UserAuthentication(1L);
    }

    @Nested
    @DisplayName("getPrincipal")
    class GetPrincipal {
        @Test
        @DisplayName("회원 식별자를 반환한다")
        void getPrincipal() {
            assertThat(userAuthentication.getPrincipal()).isEqualTo(1L);
        }
    }

    @Nested
    @DisplayName("getCredentials")
    class GetCredentials {
        @Test
        @DisplayName("null 을 반환한다.")
        void getPrincipal() {
            assertThat(userAuthentication.getCredentials()).isNull();
        }
    }

    @Nested
    @DisplayName("isAuthenticated")
    class Authorities {
        @Test
        @DisplayName("true 를 반환한다.")
        void getPrincipal() {
            assertThat(userAuthentication.isAuthenticated()).isTrue();
        }
    }
}
