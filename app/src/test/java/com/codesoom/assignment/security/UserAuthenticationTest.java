package com.codesoom.assignment.security;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class UserAuthenticationTest {

    private final UserAuthentication userAuthentication = new UserAuthentication(1L);

    @Test
    void getCredentials() {
        Assertions.assertThat(userAuthentication.getCredentials()).isNull();
    }

}