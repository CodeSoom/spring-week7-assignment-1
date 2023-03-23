package com.codesoom.assignment.application;

import com.codesoom.assignment.domain.Role;
import com.codesoom.assignment.domain.RoleRepository;
import com.codesoom.assignment.domain.User;
import com.codesoom.assignment.domain.UserRepository;
import com.codesoom.assignment.errors.LoginFailException;
import com.codesoom.assignment.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

  private final UserRepository userRepository;
  private final JwtUtil jwtUtil;
  private final PasswordEncoder passwordEncoder;
  private final RoleRepository roleRepository;

  public String login(String email, String password) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new LoginFailException(email));

    if (!user.authenticate(password, passwordEncoder)) {
      throw new LoginFailException(email);
    }

    return jwtUtil.encode(user.getId());
  }

  public Long parseToken(String accessToken) {
    Claims claims = jwtUtil.decode(accessToken);
    return claims.get("userId", Long.class);
  }

  public List<String> roleName(Long userId) {
    return userRepository.findById(userId).stream()
        .map(u -> u.getRole().getName())
        .collect(Collectors.toList());
  }
}
