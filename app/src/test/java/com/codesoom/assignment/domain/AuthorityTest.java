package com.codesoom.assignment.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AuthorityTest {

    @Test
    void createAuthorityWithBuilder() {
        Authority authority = Authority.builder()
                .authorityName("TEST")
                .build();

        assertThat(authority.getAuthorityName()).isEqualTo("TEST");
    }

}
