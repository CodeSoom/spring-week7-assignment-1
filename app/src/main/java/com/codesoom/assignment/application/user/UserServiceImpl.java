package com.codesoom.assignment.application.user;

import com.codesoom.assignment.domain.user.User;
import com.codesoom.assignment.domain.user.UserModifier;
import com.codesoom.assignment.domain.user.UserQuery;
import com.codesoom.assignment.domain.user.UserService;
import com.codesoom.assignment.dto.user.UserModificationData;
import com.codesoom.assignment.dto.user.UserRegistrationData;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final UserModifier userModifier;
    private final UserQuery userQuery;

    public UserServiceImpl(
            UserModifier userModifier,
            UserQuery userQuery
    ) {
        this.userModifier = userModifier;
        this.userQuery = userQuery;
    }


    @Override
    public void join(UserRegistrationData registrationRequest) {
        User user = registrationRequest.toEntity();
        userModifier.join(user);
    }

    @Override
    public void modify(UserModificationData modificationRequest) {

    }

    @Override
    public void leave(Long userId) {

    }

    @Override
    public User findUser(Long id) {
        return null;
    }
}
