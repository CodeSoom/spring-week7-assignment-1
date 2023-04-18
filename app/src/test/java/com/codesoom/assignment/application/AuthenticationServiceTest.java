package com.codesoom.assignment.application;

import com.codesoom.assignment.domain.Role;
import com.codesoom.assignment.domain.RoleRepository;
import com.codesoom.assignment.domain.User;
import com.codesoom.assignment.domain.UserRepository;
import com.codesoom.assignment.errors.InvalidTokenException;
import com.codesoom.assignment.errors.LoginFailException;
import com.codesoom.assignment.utils.JwtUtil;
import java.util.List;
import java.util.stream.Collectors;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class AuthenticationServiceTest {

  private static final String SECRET = "12345678901234567890123456789012";

  private static final String VALID_TOKEN = "eyJhbGciOiJIUzI1NiJ9." +
      "eyJ1c2VySWQiOjF9.ZZ3CUl0jxeLGvQ1Js5nG2Ty5qGTlqai5ubDMXZOdaDk";
  private static final String INVALID_TOKEN = "eyJhbGciOiJIUzI1NiJ9." +
      "eyJ1c2VySWQiOjF9.ZZ3CUl0jxeLGvQ1Js5nG2Ty5qGTlqai5ubDMXZOdaD0";

  private AuthenticationService authenticationService;

  private UserRepository userRepository = mock(UserRepository.class);
  private RoleRepository roleRepository = mock(RoleRepository.class);


  @BeforeEach
  void setUp() {
    JwtUtil jwtUtil = new JwtUtil(SECRET);

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    authenticationService = new AuthenticationService(
        userRepository, jwtUtil, passwordEncoder, roleRepository);

    User user = User.builder().build();
    user.changePassword("test", passwordEncoder);

    given(userRepository.findByEmail("tester@example.com"))
        .willReturn(Optional.of(user));

    given(roleRepository.findAllByUserId(1L)).willReturn(List.of(new Role("USER")));
    given(roleRepository.findAllByUserId(1004L)).willReturn(List.of(new Role("ADMIN")));
  }

  @Test
  void loginWithRightEmailAndPassword() {
    String accessToken = authenticationService.login(
        "tester@example.com", "test");

    assertThat(accessToken).isEqualTo("eyJhbGciOiJIUzI1NiJ9..Y3zwinksGMfE9Ym4QHp3jFBeDE_iJdw3F-DDlvMEE9Q");

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
  @DisplayName("권한 테스트")
  public void roles() throws Exception {
    assertThat(authenticationService.roles(1L).stream()
        .map(Role::getName).collect(Collectors.toList())).isEqualTo(List.of("USER"));

    assertThat(authenticationService.roles(1004L).stream()
        .map(Role::getName).collect(Collectors.toList())).isEqualTo(List.of("ADMIN"));
  }
}
