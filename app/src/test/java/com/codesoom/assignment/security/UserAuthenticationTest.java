package com.codesoom.assignment.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("UserAuthentication 클래스")
class UserAuthenticationTest {

    private UserAuthentication userAuthentication;

    private static final Long VALID_ID = 1L;

    @BeforeEach
    void setUp(){
        userAuthentication = new UserAuthentication(VALID_ID);
    }

    @Nested
    @DisplayName("getCredentials() 메서드는")
    class Describe_getCredentials {

        @Test
        @DisplayName("null을 리턴합니다.")
        void it_return_null() {
            assertThat(userAuthentication.getCredentials()).isNull();
        }
    }

    @Nested
    @DisplayName("getPrincipal() 메서드는")
    class Describe_getPrincipal{

        @Test
        @DisplayName("userId를 리턴합니다.")
        void it_return_userId(){
            assertThat(userAuthentication.getPrincipal()).isEqualTo(VALID_ID);
        }
    }

    @Nested
    @DisplayName("isAuthenticated() 메서드는")
    class Describe_isAuthenticated {

        @Test
        @DisplayName("true를 리턴합니다.")
        void it_return_true() {
            assertThat(userAuthentication.isAuthenticated()).isTrue();
        }
    }
}
