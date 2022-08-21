package com.codesoom.assignment.application;

import com.codesoom.assignment.domain.User;
import com.codesoom.assignment.domain.UserRepository;
import com.codesoom.assignment.dto.UserModificationData;
import com.codesoom.assignment.dto.UserRegistrationData;
import com.codesoom.assignment.errors.UserEmailDuplicationException;
import com.codesoom.assignment.errors.UserNotFoundException;
import com.github.dozermapper.core.Mapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class UserService {
    private final UserRegistrationService registrationService;
    private final UserModificationService modificationService;

    public UserService(Mapper dozerMapper, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.registrationService = new UserRegistrationService(dozerMapper, userRepository, passwordEncoder);
        this.modificationService = new UserModificationService(dozerMapper, userRepository);
    }

    public User registerUser(UserRegistrationData registrationData) {
        return registrationService.registerUser(registrationData);
    }

    public User updateUser(Long id, UserModificationData modificationData) {
        return modificationService.updateUser(id, modificationData);
    }

    public User deleteUser(Long id) {
        return modificationService.deleteUser(id);
    }
}
