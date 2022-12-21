package com.codesoom.assignment.application;

import com.codesoom.assignment.domain.Authority;
import com.codesoom.assignment.domain.User;
import com.codesoom.assignment.repository.UserRepository;
import com.codesoom.assignment.dto.UserModificationData;
import com.codesoom.assignment.dto.UserRegistrationData;
import com.codesoom.assignment.errors.UserEmailDuplicationException;
import com.codesoom.assignment.errors.UserNotFoundException;
import com.github.dozermapper.core.Mapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collections;

@Service
@Transactional
public class UserService {
    private final Mapper mapper;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public UserService(Mapper dozerMapper, PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.mapper = dozerMapper;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    public User registerUser(UserRegistrationData registrationData) {
        String email = registrationData.getEmail();
        if (userRepository.existsByEmail(email)) {
            throw new UserEmailDuplicationException(email);
        }

        Authority authority = Authority.builder()
                .authorityName("USER")
                .build();

        User user = User.builder()
                .email(registrationData.getEmail())
                .name(registrationData.getName())
                .password(registrationData.getPassword())
                .authorities(Collections.singleton(authority))
                .build();

        user.changePassword(user.getPassword(), passwordEncoder);

        return userRepository.save(user);
    }

    public User updateUser(Long id, UserModificationData modificationData) {
        User user = findUser(id);

        User source = mapper.map(modificationData, User.class);
        user.changeWith(source);
        user.changePassword(source.getPassword(), passwordEncoder);

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
