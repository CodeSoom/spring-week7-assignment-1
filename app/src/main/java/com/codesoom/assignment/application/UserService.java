package com.codesoom.assignment.application;

import com.codesoom.assignment.domain.User;
import com.codesoom.assignment.domain.UserRepository;
import com.codesoom.assignment.dto.UserModificationData;
import com.codesoom.assignment.dto.UserRegistrationData;
import com.codesoom.assignment.errors.UserEmailDuplicationException;
import com.codesoom.assignment.errors.UserNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final MyPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, MyPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(UserRegistrationData registrationData) {
        final String email = registrationData.getEmail();
        if (userRepository.existsByEmail(email)) {
            throw new UserEmailDuplicationException(email);
        }
        final String encodedPassword = passwordEncoder.getEncodedPassword(registrationData);
        final User user = User.builder()
                .name(registrationData.getName())
                .email(registrationData.getEmail())
                .password(encodedPassword)
                .build();

        return userRepository.save(user);
    }

    public User updateUser(Long id, UserModificationData modificationData) {
        final User user = findUser(id);

        final String encodedPassword = passwordEncoder.getEncodedPassword(modificationData);
        final User source = User.of(modificationData.getName(), encodedPassword);

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
