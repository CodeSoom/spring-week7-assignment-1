package com.codesoom.assignment.application;

import com.codesoom.assignment.domain.User;
import com.codesoom.assignment.domain.UserRepository;
import com.codesoom.assignment.dto.AuthenticationCreateData;
import com.codesoom.assignment.dto.AuthenticationResultData;
import com.codesoom.assignment.dto.SessionResultData;
import com.codesoom.assignment.dto.UserResultData;
import com.codesoom.assignment.errors.AuthenticationBadRequestException;
import com.codesoom.assignment.errors.InvalidTokenException;
import com.codesoom.assignment.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@DisplayName("AuthenticationService 테스트")
class AuthenticationServiceTest {
    private AuthenticationService authenticationService;
    private UserRepository userRepository;
    private JwtUtil jwtUtil;
    private PasswordEncoder passwordEncoder;

    private static final String SECRET = "12345678901234567890123456789010";
    private static final String EXISTED_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJleGlzdGVkRW1haWwifQ." +
            "UQodS3elf3Cu4g0PDFHqVloFbcKHHmTTnk0jGmiwPXY";
    private static final String NOT_EXISTED_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJleGlzdGVkRW1haWwifQ." +
            "UQodS3elf3Cu4g0PDFHqVloFbcKHHmTTnk0jGmiwPXy";

    private static final String EXISTED_EMAIL = "existedEmail";
    private static final String EXISTED_PASSWORD = "existedPassword";

    private static final String NOT_EXISTED_EMAIL = "existedEmail";
    private static final String NOT_EXISTED_PASSWORD = "existedPassword";

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        jwtUtil = mock(JwtUtil.class);
        passwordEncoder = new BCryptPasswordEncoder();
        authenticationService = new AuthenticationService(userRepository, jwtUtil, passwordEncoder);
    }

    @Nested
    @DisplayName("authenticateUser 메서드는")
    class Describe_authenticateUser {
        @Nested
        @DisplayName("만약 저장되어 있는 이메일과 비밀번호가 주어진다면")
        class Context_WithExistedEmailAndExistedPassword {
            private final String givenExistedEmail = EXISTED_EMAIL;
            private final String givenExistedPassword = EXISTED_PASSWORD;
            private User user;

            @BeforeEach
            void setUp() {
                user = User.builder()
                        .email(EXISTED_EMAIL)
                        .password(EXISTED_PASSWORD)
                        .build();
            }

            @Test
            @DisplayName("주어진 이메일과 비밀번호에 해당하는 사용자를 리턴한다")
            void itReturnsUser() {
                given(userRepository.findByEmail(givenExistedEmail)).willReturn(Optional.of(user));

                UserResultData userResultData = authenticationService.authenticateUser(givenExistedEmail, givenExistedPassword);

                assertThat(userResultData.getEmail()).isEqualTo(user.getEmail());
                assertThat(userResultData.getPassword()).isEqualTo(user.getPassword());
            }
        }

        @Nested
        @DisplayName("만약 저장되어 있지 않은 이메일이 주어진다면")
        class Context_WithNotExistedEmail {
            private final String givenNotExistedEmail = NOT_EXISTED_EMAIL;
            private final String givenExistedPassword = EXISTED_PASSWORD;

            @Test
            @DisplayName("인증 요청이 잘못되었다는 예외를 던진다")
            void itThrowsAuthenticationBadRequestException() {
                given(userRepository.findByEmail(givenNotExistedEmail)).willReturn(Optional.empty());

                assertThatThrownBy(() -> authenticationService.authenticateUser(givenNotExistedEmail,givenExistedPassword))
                        .isInstanceOf(AuthenticationBadRequestException.class)
                        .hasMessageContaining("Authentication bad request");
            }
        }

        @Nested
        @DisplayName("만약 저장되어 있지 않은 비밀번호가 주어진다면")
        class Context_WithNotExistedPassword {
            private final String givenExistedEmail = EXISTED_EMAIL;
            private final String givenNotExistedPassword = NOT_EXISTED_PASSWORD;
            private User user;

            @BeforeEach
            void setUp() {
                user = User.builder()
                        .email(givenExistedEmail)
                        .email(givenNotExistedPassword)
                        .deleted(true)
                        .build();
            }

            @Test
            @DisplayName("인증 요청이 잘못되었다는 예외를 던진다")
            void itThrowsAuthenticationBadRequestException() {
                given(userRepository.findByEmail(givenExistedEmail)).willReturn(Optional.of(user));

                assertThatThrownBy(() -> authenticationService.authenticateUser(user.getEmail(),user.getPassword()))
                        .isInstanceOf(AuthenticationBadRequestException.class)
                        .hasMessageContaining("Authentication bad request");
            }
        }
    }

    @Nested
    @DisplayName("createToken 메서드는")
    class Describe_createToken {
        @Nested
        @DisplayName("만약 저장되어 있는 사용자가 주어진다면")
        class Context_WithExistedUser {
            private AuthenticationCreateData givenUser;
            private SessionResultData sessionResultData;
            private User user;

            @BeforeEach
            void setUp() {
                givenUser = AuthenticationCreateData.builder()
                        .email(EXISTED_EMAIL)
                        .password(EXISTED_PASSWORD)
                        .build();

                user = User.builder()
                        .email(EXISTED_EMAIL)
                        .password(EXISTED_PASSWORD)
                        .build();

                sessionResultData = SessionResultData.from(EXISTED_TOKEN);
            }

            @Test
            @DisplayName("주어진 사용자를 이용하여 토큰을 생성하고 해당 토큰을 리턴한다")
            void itCreatesTokenAndReturnsToken() {
                given(userRepository.findByEmail(givenUser.getEmail())).willReturn(Optional.of(user));
                given(jwtUtil.encode(givenUser.getEmail())).willReturn(EXISTED_TOKEN);

                SessionResultData token = authenticationService.createToken(givenUser);

                assertThat(token).isEqualTo(sessionResultData);
            }
        }

        @Nested
        @DisplayName("만약 이메일이 저장되어 있지 않은 사용자가 주어진다면")
        class Context_WithUserWithoutEmail {
            private AuthenticationCreateData givenUser;

            @BeforeEach
            void setUp() {
                givenUser = AuthenticationCreateData.builder()
                        .email(NOT_EXISTED_EMAIL)
                        .password(EXISTED_PASSWORD)
                        .build();
            }

            @Test
            @DisplayName("인증 요청이 잘못 되었다는 예외를 던진다")
            void itThrowsUserNotFoundException() {
                given(userRepository.findByEmail(eq(givenUser.getEmail())))
                        .willThrow(new AuthenticationBadRequestException());

                assertThatThrownBy(() -> authenticationService.createToken(givenUser))
                        .isInstanceOf(AuthenticationBadRequestException.class)
                        .hasMessageContaining("Authentication bad request");
            }
        }

        @Nested
        @DisplayName("만약 비밀번호가 올바르지 않는 사용자가 주어진다면")
        class Context_WithUserWithoutPassword {
            private AuthenticationCreateData givenUser;

            @BeforeEach
            void setUp() {
                givenUser = AuthenticationCreateData.builder()
                        .email(EXISTED_EMAIL)
                        .password(NOT_EXISTED_PASSWORD)
                        .build();
            }

            @Test
            @DisplayName("인증 요청이 잘못 되었다는 예외를 던진다")
            void itThrowsUserNotFoundExceotuib() {
                given(userRepository.findByEmail(eq(givenUser.getEmail())))
                        .willThrow(new AuthenticationBadRequestException());

                assertThatThrownBy(() -> authenticationService.createToken(givenUser))
                        .isInstanceOf(AuthenticationBadRequestException.class)
                        .hasMessageContaining("Authentication bad request");
            }
        }
    }

    @Nested
    @DisplayName("parseToken 메서드는")
    class Describe_parseToken {
        @Nested
        @DisplayName("만약 유효한 토큰이 주어진다면")
        class Context_WithValidToken {
            private final String givenValidToken = EXISTED_TOKEN;
            private Claims claims;

            @BeforeEach
            void setUp() {
                claims = Jwts.parserBuilder()
                        .setSigningKey(Keys.hmacShaKeyFor(SECRET.getBytes()))
                        .build()
                        .parseClaimsJws(EXISTED_TOKEN)
                        .getBody();
            }

            @Test
            @DisplayName("주어진 토큰을 해석하여 안에 담긴 내용을 리턴한다")
            void itParsesTokenAndReturnsUser() {
                given(jwtUtil.decode(givenValidToken)).willReturn(claims);

                AuthenticationResultData authenticationResultData = authenticationService.parseToken(givenValidToken);

                assertThat(authenticationResultData.getEmail()).isEqualTo(EXISTED_EMAIL);
            }
        }

        @Nested
        @DisplayName("만약 유효하지 않은 토큰이 주어진다면")
        class Context_WithNotValidToken {
            private final String givenNotValidToken = NOT_EXISTED_TOKEN;

            @Test
            @DisplayName("토큰이 유효하지 않다는 예외를 던진다")
            void itThrowsInvalidTokenException() {
                given(jwtUtil.decode(givenNotValidToken))
                        .willThrow(new InvalidTokenException(givenNotValidToken));

                assertThatThrownBy(() -> authenticationService.parseToken(givenNotValidToken))
                        .isInstanceOf(InvalidTokenException.class)
                        .hasMessageContaining("Invalid token");
            }
        }

        @Nested
        @DisplayName("만약 null 이 주어진다면")
        class Context_WithNull {
            @Test
            @DisplayName("토큰이 유효하지 않다는 예외를 던진다")
            void itThrowsInvalidTokenException() {
                given(jwtUtil.decode(null))
                        .willThrow(new InvalidTokenException(null));

                assertThatThrownBy(() -> authenticationService.parseToken(null))
                        .isInstanceOf(InvalidTokenException.class)
                        .hasMessageContaining("Invalid token");
            }
        }
    }
}


//package com.codesoom.assignment.application;
//
//import com.codesoom.assignment.domain.User;
//import com.codesoom.assignment.domain.UserRepository;
//import com.codesoom.assignment.errors.InvalidTokenException;
//import com.codesoom.assignment.errors.LoginFailException;
//import com.codesoom.assignment.utils.JwtUtil;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.assertThatThrownBy;
//import static org.mockito.BDDMockito.given;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.verify;
//
//class AuthenticationServiceTest {
//    private static final String SECRET = "12345678901234567890123456789012";
//
//    private static final String VALID_TOKEN = "eyJhbGciOiJIUzI1NiJ9." +
//            "eyJ1c2VySWQiOjF9.ZZ3CUl0jxeLGvQ1Js5nG2Ty5qGTlqai5ubDMXZOdaDk";
//    private static final String INVALID_TOKEN = "eyJhbGciOiJIUzI1NiJ9." +
//            "eyJ1c2VySWQiOjF9.ZZ3CUl0jxeLGvQ1Js5nG2Ty5qGTlqai5ubDMXZOdaD0";
//
//    private AuthenticationService authenticationService;
//
//    private UserRepository userRepository = mock(UserRepository.class);
//
//    @BeforeEach
//    void setUp() {
//        JwtUtil jwtUtil = new JwtUtil(SECRET);
//        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//
//        authenticationService = new AuthenticationService(
//                userRepository, jwtUtil, passwordEncoder);
//
//        User user = User.builder()
//                 .id(1L)
//                .build();
//        user.updatePassword("test", passwordEncoder);
//
//        given(userRepository.findByEmail("tester@example.com"))
//                .willReturn(Optional.of(user));
//    }
//
//    @Test
//    void loginWithRightEmailAndPassword() {
//        String accessToken = authenticationService.createToken(
//                "tester@example.com", "test");
//
//        assertThat(accessToken).isEqualTo(VALID_TOKEN);
//
//        verify(userRepository).findByEmail("tester@example.com");
//    }
//
//    @Test
//    void loginWithWrongEmail() {
//        assertThatThrownBy(
//                () -> authenticationService.createToken("badguy@example.com", "test")
//        ).isInstanceOf(LoginFailException.class);
//
//        verify(userRepository).findByEmail("badguy@example.com");
//    }
//
//    @Test
//    void loginWithWrongPassword() {
//        assertThatThrownBy(
//                () -> authenticationService.createToken("tester@example.com", "xxx")
//        ).isInstanceOf(LoginFailException.class);
//
//        verify(userRepository).findByEmail("tester@example.com");
//    }
//
//    @Test
//    void parseTokenWithValidToken() {
//        Long userId = authenticationService.parseToken(VALID_TOKEN);
//
//        assertThat(userId).isEqualTo(1L);
//    }
//
//    @Test
//    void parseTokenWithInvalidToken() {
//        assertThatThrownBy(
//                () -> authenticationService.parseToken(INVALID_TOKEN)
//        ).isInstanceOf(InvalidTokenException.class);
//    }
//}
