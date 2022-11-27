package com.codesoom.assignment.user.application;

import com.codesoom.assignment.session.domain.Role;
import com.codesoom.assignment.session.domain.RoleRepository;
import com.codesoom.assignment.user.application.exception.UserEmailDuplicationException;
import com.codesoom.assignment.user.application.exception.UserNotFoundException;
import com.codesoom.assignment.user.domain.User;
import com.codesoom.assignment.user.domain.UserRepository;
import com.codesoom.assignment.user.presentation.dto.UserModificationData;
import com.codesoom.assignment.user.presentation.dto.UserRegistrationData;
import com.github.dozermapper.core.Mapper;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class UserService {
    private final Mapper mapper;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserService(final Mapper dozerMapper,
                       final UserRepository userRepository,
                       RoleRepository roleRepository) {
        this.mapper = dozerMapper;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public User registerUser(final UserRegistrationData registrationData) {
        String email = registrationData.getEmail();

        if (userRepository.existsByEmail(email)) {
            throw new UserEmailDuplicationException(email);
        }

        User user = userRepository.save(
                mapper.map(registrationData, User.class)
        );

        roleRepository.save(
                new Role(user.getId(), "USER")
        );

        return user;
    }

    public User updateUser(final Long id,
                           final UserModificationData modificationData) {
        User user = findUser(id);

        User source = mapper.map(modificationData, User.class);
        user.changeWith(source);
        return user;
    }

    public User deleteUser(final Long id) {
        User user = findUser(id);
        user.destroy();
        return user;
    }

    private User findUser(final Long id) {
        return userRepository.findByIdAndDeletedIsFalse(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }
}
