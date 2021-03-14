package com.codesoom.assignment.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserAuthenticationTest {
    private final long givenID = 1L;
    private final UserAuthentication givenUserAuthentication = new UserAuthentication(givenID);

    @Nested
    @DisplayName("toString 메서드는")
    class Describe_toString {
        @Test
        @DisplayName("userId 가 담인 메시지 를 리턴한다,")
        void It_returns_message_with_userId() {
            assertThat(givenUserAuthentication.toString())
                    .isEqualTo(String.format("UserAuthentication(%d)", givenID));
        }
    }

    @Nested
    @DisplayName("getCredentials 메서드는")
    class Describe_getCredentials {
        @Test
        @DisplayName("null 을 리턴한다.")
        void It_returns_null() {
            assertThat(givenUserAuthentication.getCredentials())
                    .isNull();
        }
    }
}
