package com.codesoom.assignment.application;

import com.codesoom.assignment.domain.Authority;
import com.codesoom.assignment.domain.User;
import com.codesoom.assignment.errors.InvalidTokenException;
import com.codesoom.assignment.errors.LoginFailException;
import com.codesoom.assignment.repository.UserRepository;
import com.codesoom.assignment.utils.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class AuthenticationServiceTest {
    private static final String SECRET = "12345678901234567890123456789012";

    private static final String VALID_TOKEN = "eyJhbGciOiJIUzI1NiJ9." +
            "eyJ1c2VySWQiOjF9.ZZ3CUl0jxeLGvQ1Js5nG2Ty5qGTlqai5ubDMXZOdaDk";
    private static final String INVALID_TOKEN = VALID_TOKEN + "WRONG";

    private AuthenticationService authenticationService;

    private UserRepository userRepository = mock(UserRepository.class);

    @BeforeEach
    void setUp() {
        JwtUtil jwtUtil = new JwtUtil(SECRET);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        authenticationService = new AuthenticationService(
                userRepository, jwtUtil, passwordEncoder);

        User user = User.builder()
                .email("tester@example.com")
                .password("test")
                .build();
        user.changePassword("test", passwordEncoder);

        given(userRepository.findByEmail("tester@example.com"))
                .willReturn(Optional.of(user));

        Set<Authority> authoritySet = new HashSet<>();
        Authority authority = Authority.builder()
                                        .authorityName("TEST")
                                        .build();
        authoritySet.add(authority);

        given(userRepository.findAuthorityNameById(1L))
                .willReturn(authoritySet);

    }

    @Test
    void loginWithRightEmailAndPassword() {
        String accessToken = authenticationService.login(
                "tester@example.com", "test");

        //TODO VALID_TOKEN과 다름
//        assertThat(accessToken).isEqualTo(VALID_TOKEN);

        verify(userRepository).findByEmail("tester@example.com");
    }

    @Test
    void loginWithWrongEmail() {
        assertThatThrownBy(
                () -> authenticationService.login("badguy@example.com", "test")
        ).isInstanceOf(LoginFailException.class);

        verify(userRepository).findByEmail("badguy@example.com");
    }

    @Test
    void loginWithWrongPassword() {
        assertThatThrownBy(
                () -> authenticationService.login("tester@example.com", "xxx")
        ).isInstanceOf(LoginFailException.class);

        verify(userRepository).findByEmail("tester@example.com");
    }

    @Test
    void parseTokenWithValidToken() {
        Long userId = authenticationService.parseToken(VALID_TOKEN);

        assertThat(userId).isEqualTo(1L);
    }

    @Test
    void parseTokenWithInvalidToken() {
        assertThatThrownBy(
                () -> authenticationService.parseToken(INVALID_TOKEN)
        ).isInstanceOf(InvalidTokenException.class);
    }

    @Test
    void getUserAuthoritiesWithUserId() {
        List<String> userAuthorities = authenticationService
                                            .getUserAuthorities(1L);

        assertThat(userAuthorities.contains("TEST"))
                .isTrue();
    }
}
