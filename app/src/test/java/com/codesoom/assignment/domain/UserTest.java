package com.codesoom.assignment.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {
  private PasswordEncoder passwordEncoder;

  @BeforeEach
  void setUp(){
      passwordEncoder = new BCryptPasswordEncoder();
  }
  @Test
  void changeWith() {

    User user = User.builder().build();

    user.changeWith(User.builder()
        .name("TEST")
        .password("TEST")
        .build());

    assertThat(user.getName()).isEqualTo("TEST");
    assertThat(user.getPassword()).isEqualTo("");
  }

  @Test
  @DisplayName("비밀변호 변경")
  public void changePassword() throws Exception {
    User user = User.builder().build();

    user.changePassword("TEST",passwordEncoder);

    assertThat(user.getPassword()).isNotEmpty();
    assertThat(user.getName()).isNotEqualTo("TEST");
  }

  @Test
  void destroy() {
    User user = User.builder().build();

    assertThat(user.isDeleted()).isFalse();

    user.destroy();

    assertThat(user.isDeleted()).isTrue();
  }

  @Test
  void authenticate() {
    User user = User.builder().build();
    user.changePassword("TEST",passwordEncoder);

    assertThat(user.authenticate("TEST",passwordEncoder)).isTrue();
    assertThat(user.authenticate("xxx",passwordEncoder)).isFalse();
  }

  @Test
  void authenticateWithDeletedUser() {
    User user = User.builder().deleted(true).build();
    user.changePassword("TEST",passwordEncoder);

    assertThat(user.authenticate("TEST",passwordEncoder)).isFalse();
    assertThat(user.authenticate("xxx",passwordEncoder)).isFalse();
  }
}
