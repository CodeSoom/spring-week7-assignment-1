package com.codesoom.assignment.application;

import com.codesoom.assignment.domain.Role;
import com.codesoom.assignment.domain.RoleRepository;
import com.codesoom.assignment.domain.User;
import com.codesoom.assignment.domain.UserRepository;
import com.codesoom.assignment.dto.UserModificationData;
import com.codesoom.assignment.dto.UserRegistrationData;
import com.codesoom.assignment.errors.UserEmailDuplicationException;
import com.codesoom.assignment.errors.UserNotFoundException;
import com.github.dozermapper.core.Mapper;
import java.nio.file.AccessDeniedException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

  private final Mapper mapper;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final RoleRepository roleRepository;


  public User registerUser(UserRegistrationData registrationData) {
    String email = registrationData.getEmail();
    if (userRepository.existsByEmail(email)) {
      throw new UserEmailDuplicationException(email);
    }
//    User user = mapper.map(registrationData, User.class);
    User user = userRepository.save(
        mapper.map(registrationData, User.class));
    Role role = roleRepository.save(new Role(user.getId(), "USER"));
    user.changePassword(registrationData.getPassword(), passwordEncoder);

    return user;
  }


  public User updateUser(Long id, UserModificationData modificationData, Long userId)
      throws AccessDeniedException {
    if (id != userId) {
      throw new AccessDeniedException("권한이 없습니다.");
    }

    User user = findUser(id);

    User source = mapper.map(modificationData, User.class);
    user.changeWith(source);

    return user;
  }

  public User deleteUser(Long id) {
    User user = findUser(id);
    user.destroy();
    return user;
  }

  private User findUser(Long id) {
    return userRepository.findByIdAndDeletedIsFalse(id)
        .orElseThrow(() -> new UserNotFoundException(id));
  }
}
