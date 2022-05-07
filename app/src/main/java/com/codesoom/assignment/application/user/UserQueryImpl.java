package com.codesoom.assignment.application.user;

import com.codesoom.assignment.domain.user.User;
import com.codesoom.assignment.domain.user.UserQuery;
import com.codesoom.assignment.domain.user.UserQueryValidator;
import com.codesoom.assignment.domain.user.UserRepository;
import com.codesoom.assignment.errors.user.UserNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class UserQueryImpl implements UserQuery {
    private final UserRepository userRepository;
    private final UserQueryValidator userQueryValidator;

    public UserQueryImpl(UserRepository userRepository, UserQueryValidator userQueryValidator) {
        this.userRepository = userRepository;
        this.userQueryValidator = userQueryValidator;
    }

    @Override
    public User findUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException());
        return user;
    }

    @Override
    public User findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException());

        return user;
    }
}
