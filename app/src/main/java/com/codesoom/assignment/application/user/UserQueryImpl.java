package com.codesoom.assignment.application.user;

import com.codesoom.assignment.domain.user.User;
import com.codesoom.assignment.domain.user.UserQuery;
import com.codesoom.assignment.domain.user.UserRepository;
import com.codesoom.assignment.errors.user.UserNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class UserQueryImpl implements UserQuery {
    private final UserRepository userRepository;

    public UserQueryImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
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
