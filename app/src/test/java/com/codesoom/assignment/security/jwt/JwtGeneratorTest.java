package com.codesoom.assignment.security.jwt;

import com.codesoom.assignment.common.message.ErrorMessage;
import com.codesoom.assignment.errors.CustomInternalServerException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ContextConfiguration(initializers = ConfigFileApplicationContextInitializer.class)
@TestPropertySource("classpath:application-jwt-test.yml")
class JwtGeneratorTest {

    @Autowired
    private JwtGenerator jwtGenerator;

    @Autowired
    private JwtParser jwtParser;



    @Nested
    @DisplayName("JWT 토큰")
    class Describe_jwt {
        @Nested
        @DisplayName("적합한 하지 않은 값 주어진다면[빈값]")
        class Context_with_empty_email {

            @ParameterizedTest
            @ValueSource(strings = {""})
            @DisplayName("[유저 이메일이 반값이므로 토큰을 생성할 수 없습니다] 서버 예외 발생한다")
            void will_throw_exception(String email) {
                assertThatThrownBy(() -> jwtGenerator.generateToken(email))
                        .isInstanceOf(CustomInternalServerException.class)
                        .hasMessageContaining(ErrorMessage.EMPTY_USER_EMAIL_TOKEN.getErrorMsg());
            }
        }

        @Nested
        @DisplayName("적합한 값이 주어진다면[이메일]")
        class Context_with_email {

            @ParameterizedTest
            @ValueSource(strings = {"lbs243120@gamil.com", "dev.bslee@gmail", "lbs243@naver.com"})
            @DisplayName("생성된다")
            void will_generate_token(String email) {
                String token = jwtGenerator.generateToken(email);
                String userEmailFromToken = jwtParser.getUserEmailFromToken(token);

                assertThat(userEmailFromToken).isEqualTo(email);
            }
        }
    }
}