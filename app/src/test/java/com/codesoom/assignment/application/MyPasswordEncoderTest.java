package com.codesoom.assignment.application;

import com.codesoom.assignment.config.AppConfig;
import com.codesoom.assignment.dto.UserRegistrationData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MyPasswordEncoderTest {

    private MyPasswordEncoder passwordEncoder;
    private AppConfig appConfig;

    @BeforeEach
    void setUp() {
        appConfig = new AppConfig();
        passwordEncoder = new MyPasswordEncoder(appConfig.passwordEncoder());
    }

    @DisplayName("비밀번호를 암호화하면 입력받은 비밀번호가 변경된다")
    @Test
    void encodePassword() {
        String expected = "1234";
        UserRegistrationData request = UserRegistrationData.builder()
                .password(expected)
                .build();

        String actual = passwordEncoder.getEncodedPassword(request);
        assertThat(actual).isNotEqualTo(expected);
    }
}
