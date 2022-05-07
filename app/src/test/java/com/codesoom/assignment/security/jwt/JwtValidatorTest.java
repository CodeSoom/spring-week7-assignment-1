package com.codesoom.assignment.security.jwt;

import com.codesoom.assignment.common.message.ErrorMessage;
import com.codesoom.assignment.errors.authentication.InvalidTokenException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import static org.assertj.core.api.Assertions.assertThatThrownBy;


@ContextConfiguration(initializers = ConfigFileApplicationContextInitializer.class)
@SpringBootTest(properties = "spring.config.location=classpath:/application-jwt-test.yml")
class JwtValidatorTest {
    @Autowired
    private JwtGenerator jwtGenerator;

    @Autowired
    private JwtValidator jwtValidator;

    @Nested
    @DisplayName("발급된 JWT 토큰은")
    class Describe_generated_jwt {
        private String tokenUserEmail = "dev.bslee@gmail.com";
        private String token;

        @BeforeEach
        void setUp() {
            token = jwtGenerator.generateToken(tokenUserEmail);

        }

        @Nested
        @DisplayName("유효 시간이 지나면")
        class Context_after_expire_date {
            @Test
            @DisplayName("[만료된 토큰입니다] 예외가 발생한다")
            void will_throw_exception() throws InterruptedException {
                Thread.sleep(2000);
                assertThatThrownBy(() -> jwtValidator.validateToken(token, tokenUserEmail))
                        .isInstanceOf(InvalidTokenException.class)
                        .hasMessageContaining(ErrorMessage.EXPIRED_TOKEN.getErrorMsg());
            }
        }

        @Nested
        @DisplayName("유효하지 않은 토큰이라면")
        class Context_invalid_token {
            @Test
            @DisplayName("[토크 검증에 실패했습니다] 예외가 발생한다")
            void will_throw_exception() {
                assertThatThrownBy(() -> jwtValidator.validateToken("", tokenUserEmail))
                        .isInstanceOf(InvalidTokenException.class)
                        .hasMessageContaining(ErrorMessage.INVALID_TOKEN.getErrorMsg());
            }
        }

        @Nested
        @DisplayName("발급받은 유저와 인증할 유저가 다르다")
        class Context_dont_compare {
            @Test
            @DisplayName("[인증되지 않은 사용자입니다] 예외가 발생한다")
            void will_throw_exception() {
                tokenUserEmail=tokenUserEmail+"test";
                assertThatThrownBy(() -> jwtValidator.validateToken(token, tokenUserEmail))
                        .isInstanceOf(InvalidTokenException.class)
                        .hasMessageContaining(ErrorMessage.INVALID_USER_BY_TOKEN.getErrorMsg());
            }
        }
    }
}