package com.codesoom.assignment.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserAuthenticationTest {
    private UserAuthentication userAuthentication;

    @BeforeEach
    void seup(){
        userAuthentication = new UserAuthentication(1L);
    }

    @Nested
    @DisplayName("getPrincipal메소드는")
    class GetPrincipal{
        @Test
        @DisplayName("회원식별자를 반환한다.")
        void getPrincipal(){
            assertThat(userAuthentication.getPrincipal()).isEqualTo(1L);
        }
    }

    @Nested
    @DisplayName("getCredentials메소드는")
    class GetCredentials {
        @Test
        @DisplayName("null을 반환한다.")
        void getPrincipal() {
            assertThat(userAuthentication.getCredentials()).isNull();
        }
    }

    @Nested
    @DisplayName("isAuthenticated메소드는")
    class Authorities {
        @Test
        @DisplayName("true 를 반환한다.")
        void getPrincipal() {
            assertThat(userAuthentication.isAuthenticated()).isTrue();
        }
    }


}
