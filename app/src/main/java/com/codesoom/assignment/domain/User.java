package com.codesoom.assignment.domain;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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


  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "ROLE_ID")
  @Builder.Default
  private Role role = Role.builder()
      .name("USER")
      .build();


  public void changeWith(User source) {
    name = source.name;
  }



  public void changePassword(String password,
      PasswordEncoder passwordEncoder) {
    this.password = passwordEncoder.encode(password);
  }

  public void destroy() {
    deleted = true;
  }

  public boolean authenticate(String password,
      PasswordEncoder passwordEncoder) {
    return !deleted && passwordEncoder.matches(password, this.password);
  }

  public User(String email, String name, String password, Role role) {
    this.email = email;
    this.name = name;
    this.password = password;
    this.role = role;
  }
}
