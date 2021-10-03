package com.codesoom.assignment.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserAuthenticationTest {
    private UserAuthentication userAuthentication;

    @BeforeEach
    void setUp() {
        userAuthentication = new UserAuthentication(1L);
    }

    @Nested
    @DisplayName("getPrincipal()을 실행하면")
    class GetPrincipal {
        @Test
        @DisplayName("id를 반환한다.")
        void getPrincipal() {
            assertThat(userAuthentication.getPrincipal()).isEqualTo(1L);
        }
    }

    @Nested
    @DisplayName("getCredentials()를 실행하면")
    class GetCredentials {
        @Test
        @DisplayName("null을 반환한다.")
        void getPrincipal() {
            assertThat(userAuthentication.getCredentials()).isNull();
        }
    }

    @Nested
    @DisplayName("isAuthenticated()를 실행하면")
    class IsAuthenticated {
        @Test
        @DisplayName("true 를 반환한다.")
        void getPrincipal() {
            assertThat(userAuthentication.isAuthenticated()).isTrue();
        }
    }

}