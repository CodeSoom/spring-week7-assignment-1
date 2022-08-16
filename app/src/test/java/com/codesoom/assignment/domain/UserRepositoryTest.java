package com.codesoom.assignment.domain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class UserRepositoryTest {
    public static final String EMAIL = "qjawlsqjacks@naver.com";
    public static final String PASSWORD = "1234";
    public static final String NAME = "박범진";
    private static final User USER = User.builder()
            .email(EMAIL)
            .password(PASSWORD)
            .name(NAME)
            .build();

    @Autowired
    private UserRepository userRepository;

    @Test
    void existsByEmailTest() {
        userRepository.save(USER);

        assertTrue(userRepository.existsByEmail(EMAIL));
    }
}
