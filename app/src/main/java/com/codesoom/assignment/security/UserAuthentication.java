package com.codesoom.assignment.security;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class UserAuthentication extends AbstractAuthenticationToken {

  private final Long userId;

  public UserAuthentication(Long userId, List<String> roles) {
    super(authorities(roles));
    this.userId = userId;
  }


  @Override
  public Object getCredentials() {
    return null;
  }

  @Override
  public String toString() {
    return String.format("UserAuthentication{userId=%d}", userId);
  }

  @Override
  public boolean isAuthenticated() {
    return true;
  }

  @Override
  public Object getPrincipal() {
    return userId;
  }

  private static List<GrantedAuthority> authorities(List<String> roles) {
    return roles.stream()
        .map(SimpleGrantedAuthority::new)
        .collect(Collectors.toList());
  }

}

