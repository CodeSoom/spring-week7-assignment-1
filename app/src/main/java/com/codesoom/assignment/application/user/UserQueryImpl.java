package com.codesoom.assignment.application.user;

import com.codesoom.assignment.domain.user.User;
import com.codesoom.assignment.domain.user.UserQuery;
import com.codesoom.assignment.domain.user.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class UserQueryImpl implements UserQuery {
    private final UserRepository userRepository;

    public UserQueryImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User findUser(Long userId) {
        return null;
    }
}
