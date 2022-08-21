package com.codesoom.assignment.application;

import com.codesoom.assignment.domain.User;
import com.codesoom.assignment.domain.UserRepository;
import com.codesoom.assignment.dto.UserModificationData;
import com.codesoom.assignment.errors.UserNotFoundException;
import com.github.dozermapper.core.Mapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserModificationService {
    private final Mapper dozerMapper;
    private final UserRepository userRepository;

    public UserModificationService(Mapper dozerMapper, UserRepository userRepository) {
        this.dozerMapper = dozerMapper;
        this.userRepository = userRepository;
    }

    public User updateUser(Long id, UserModificationData modificationData) {
        User user = findUser(id);

        User source = dozerMapper.map(modificationData, User.class);
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
