package com.codesoom.assignment.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {

  @Id
  @GeneratedValue
  private Long id;

  @Builder.Default
  private String email = "";

  @Builder.Default
  private String name = "";

  @Builder.Default
  private String password = "";

  @Builder.Default
  private boolean deleted = false;

  public void changeWith(User source) {
    name = source.name;
//    password = source.password;
  }


  public void destroy() {
    deleted = true;
  }

  public boolean authenticate(String password,
      PasswordEncoder passwordEncoder) {
     passwordEncoder = new BCryptPasswordEncoder();
    return !deleted && passwordEncoder.matches(password, this.password);
  }

  public void changePassword(String password,
      PasswordEncoder passwordEncoder) {
     passwordEncoder = new BCryptPasswordEncoder();
    this.password = passwordEncoder.encode(password);
  }
}
