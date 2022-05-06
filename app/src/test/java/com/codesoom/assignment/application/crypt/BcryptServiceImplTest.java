package com.codesoom.assignment.application.crypt;

import com.codesoom.assignment.domain.crypt.CryptService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

class BcryptServiceImplTest {
    private CryptService cryptService;

    @BeforeEach
    public void setUp() {
        cryptService = new BcryptServiceImpl(new BCryptPasswordEncoder());
    }

    @Nested
    @DisplayName("encode 메서드는")
    class Describe_encode {
        private String plainPassword = "12222333";

        @Test
        @DisplayName("암호화된 비밀번호를 반환한다")
        void It_returns_encode_password() {
            assertThat(cryptService.encode(plainPassword), not(equalTo(plainPassword)));
        }
    }
}