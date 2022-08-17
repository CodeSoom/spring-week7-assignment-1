package com.codesoom.assignment.application;

import com.codesoom.assignment.domain.User;
import com.codesoom.assignment.domain.UserRepository;
import com.codesoom.assignment.dto.SessionRequestData;
import com.codesoom.assignment.dto.SessionResponseData;
import com.codesoom.assignment.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@DisplayName("AuthenticationService 클래스의")
class AuthenticationServiceTest {
    private static final String SECRET = "12345678901234567890123456789012";

    private static final String VALID_TOKEN = "eyJhbGciOiJIUzI1NiJ9." +
            "eyJ1c2VySWQiOjF9.ZZ3CUl0jxeLGvQ1Js5nG2Ty5qGTlqai5ubDMXZOdaDk";
    private static final String INVALID_TOKEN = "eyJhbGciOiJIUzI1NiJ9." +
            "eyJ1c2VySWQiOjF9.ZZ3CUl0jxeLGvQ1Js5nG2Ty5qGTlqai5ubDMXZOdaD0";
    public static final String EMAIL = "qjawlsqjacks@naver.com";
    public static final String NAME = "박범진";
    public static final String PASSWORD = "1234";
    public static final User USER = User.builder()
            .id(1L)
            .email(EMAIL)
            .name(NAME)
            .password(PASSWORD)
            .build();

    private AuthenticationService authenticationService;
    private UserRepository userRepository;
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil(SECRET);
        userRepository = mock(UserRepository.class);
        authenticationService = new AuthenticationService(userRepository, jwtUtil);
    }

    @Nested
    @DisplayName("login 메서드는")
    class Describe_login {
        @Nested
        @DisplayName("입력한 로그인 정보와 일치하는 유저가 있다면")
        class Context_with_validLoginData {
            private SessionRequestData requestData;
            @BeforeEach
            void prepare() {
                requestData = new SessionRequestData(EMAIL, PASSWORD);
                given(userRepository.findByEmail(EMAIL))
                        .willReturn(Optional.of(USER));
            }
            @Test
            @DisplayName("토큰을 생성하고 리턴한다")
            void It_returns_createdToken() {
                SessionResponseData token = authenticationService.login(requestData);

                Claims decode = jwtUtil.decode(token.getAccessToken());

                assertThat(decode)
                        .containsEntry("iss", "BJP")
                        .containsEntry("userId", 1)
                        .containsEntry("role", "USER");

                verify(userRepository).findByEmail(EMAIL);
            }
        }
    }
}
