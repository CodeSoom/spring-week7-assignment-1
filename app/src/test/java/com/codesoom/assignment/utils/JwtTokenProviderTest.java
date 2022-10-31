package com.codesoom.assignment.utils;

import com.codesoom.assignment.errors.InvalidTokenException;
import com.codesoom.assignment.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@SpringBootTest
@DisplayName("JwtTokenProvider 클래스")
class JwtTokenProviderTest {

    @Value("${jwt.secret}")
    private String SECRET;

    private static final String VALID_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjF9.neCsyNLzy3lQ4o2yliotWT06FwSGZagaHpKdAkjnGGw";
    private static final String INVALID_TOKEN = "eyJhbGciOiJIUzI1NiJ9." +
            "eyJ1c2VySWQiOjF9.ZZ3CUl0jxeLGvQ1Js5nG2Ty5qGTlqai5ubDMXZOdaD0";

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Nested
    @DisplayName("createToken 메소드는")
    class Describe_createToken {
        @Nested
        @DisplayName("사용자ID가 주어지면")
        class Context_with_user_id {
            private final Long USER_ID = 1L;

            @Test
            @DisplayName("Jwt토큰을 생성해서 리턴한다")
            void it_returns_access_token() {
                String jwtToken = jwtTokenProvider.createToken(USER_ID);

                Long actualUserId = jwtTokenProvider.getUserId(jwtToken);

                assertThat(actualUserId).isEqualTo(USER_ID);
            }
        }
    }

    @Nested
    @DisplayName("getUserId 메소드는")
    class Describe_getUserId {
        @Nested
        @DisplayName("유효한 토큰이 주어지면")
        class Context_with_valid_token {
            private final Long USER_ID = 1L;
            private final String givenToken = jwtTokenProvider.createToken(USER_ID);

            @Test
            @DisplayName("복호화하고 사용자ID를 리턴한다")
            void it_returns_user_id() {
                Long actualUserId = jwtTokenProvider.getUserId(givenToken);

                assertThat(actualUserId).isEqualTo(USER_ID);

            }
        }

        @Nested
        @DisplayName("유효하지않는 토큰이 주어지면")
        class Context_with_invalid_token {
            private final Long USER_ID = 1L;
            private final String INVALID_TOKEN = jwtTokenProvider.createToken(USER_ID) + "invalid";

            @Test
            @DisplayName("예외를 던진다")
            void it_throws_exception() {
                assertThatThrownBy(() -> jwtTokenProvider.getUserId(INVALID_TOKEN)).isInstanceOf(InvalidTokenException.class);
            }
        }

        @Nested
        @DisplayName("토큰에 빈 값이 주어지면")
        class Context_with_null_or_blank {
            private final List<String> tokens = new ArrayList<>();

            @BeforeEach
            void prepare() {
                tokens.add(" ");
                tokens.add(null);
            }

            @Test
            @DisplayName("예외를 던진다")
            void it_throws_exception() {
                tokens.forEach(this::test);
            }

            private void test(String token) {
                assertThatThrownBy(() -> jwtTokenProvider.getUserId(token)).isInstanceOf(InvalidTokenException.class);
            }
        }
    }

    @Nested
    @DisplayName("getAuthentication 메소드는")
    class Describe_getAuthentication {
        @Nested
        @DisplayName("토큰이 주어지면")
        class Context_access_token {
            private final Long USER_ID = 1L;
            private final String TOKEN = jwtTokenProvider.createToken(USER_ID);

            @Test
            @DisplayName("인증 정보를 리턴한다")
            void it_returns_authentication() {
                Authentication authentication = jwtTokenProvider.getAuthentication(TOKEN);

                assertThat(authentication).isInstanceOf(Authentication.class);

            }
        }
    }

    @Nested
    @DisplayName("getHeaderAuthorization 메소드는")
    class Describe_getHeaderAuthorization {
        @Nested
        @DisplayName("헤더에 Authorization이 주어지면")
        class Context_with_request {
            @MockBean
            private HttpServletRequest request;
            private final String AUTHORIZATION_HEADER = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjF9.neCsyNLzy3lQ4o2yliotWT06FwSGZagaHpKdAkjnGGw";
            @BeforeEach
            void prepare() {
                given(request.getHeader("Authorization")).willReturn(AUTHORIZATION_HEADER);
            }
            @Test
            @DisplayName("파싱해서 리턴한다")
            void it_returns_authorization_header() {
                String actual = jwtTokenProvider.getHeaderAuthorization(request);

                assertThat(actual).isEqualTo(AUTHORIZATION_HEADER);

            }
        }
    }

}
